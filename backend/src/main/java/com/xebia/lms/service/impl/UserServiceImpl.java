/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.AppUser;
import com.xebia.lms.dto.user.*;
import com.xebia.lms.mapper.UserMapper;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.service.UserService;
import com.xebia.lms.service.AuditService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing user account lifecycle operations.
 *
 * Implements standard transactions, CRUD mappings, status adjustments, and permission resolutions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Attempting to create a new user onboarding request: {}", request.email());

        Optional<AppUser> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            log.warn("User creation aborted. Email already registered: {}", request.email());
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        AppUser user = userMapper.convertToEntity(request);

        // On invited user registration, assign a secure default password.
        // Hashing ensures raw passwords never leak or appear in data structures.
        String rawPassword = "DefaultPassword@123!";
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        AppUser savedUser = userRepository.save(user);
        log.info("User created successfully. ID: {}, Email: {}", savedUser.getUserId(), savedUser.getEmail());

        auditService.log(null, "USER_CREATED", "AppUser",
                savedUser.getUserId().toString(), AuditServiceImpl.resolveClientIp(),
                "{\"email\":\"" + savedUser.getEmail() + "\",\"displayName\":\"" + savedUser.getDisplayName() + "\"}");

        return userMapper.convertToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.info("Retrieving user by ID: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        return userMapper.convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile details for user: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        user.setDisplayName(request.displayName());
        user.setTimezone(request.timezone());

        AppUser updatedUser = userRepository.save(user);
        log.info("User details updated successfully. ID: {}", userId);

        return userMapper.convertToResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse changeUserStatus(UUID userId, ChangeStatusRequest request) {
        log.info("Changing status for user ID: {} to: {}", userId, request.status());

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        user.setStatus(request.status());

        // Permission version is incremented so that
        // Redis cache entries become immediately invalid.
        user.setPermissionVersion(user.getPermissionVersion() + 1);

        AppUser updatedUser = userRepository.save(user);
        log.info("User status successfully modified. ID: {}, Version: {}", userId, updatedUser.getPermissionVersion());

        auditService.log(null, "USER_STATUS_CHANGED", "AppUser",
                userId.toString(), AuditServiceImpl.resolveClientIp(),
                "{\"newStatus\":\"" + request.status() + "\"}");

        return userMapper.convertToResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse assignUserRole(UUID userId, AssignRoleRequest request) {
        log.info("Assigning role ID: {} to user ID: {}", request.roleId(), userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        user.setRoleId(request.roleId());

        // Permission version is incremented so that
        // Redis cache entries become immediately invalid.
        user.setPermissionVersion(user.getPermissionVersion() + 1);

        AppUser updatedUser = userRepository.save(user);
        log.info("User role successfully modified. ID: {}, Version: {}", userId, updatedUser.getPermissionVersion());

        auditService.log(null, "USER_ROLE_ASSIGNED", "AppUser",
                userId.toString(), AuditServiceImpl.resolveClientIp(),
                "{\"newRoleId\":\"" + request.roleId() + "\"}");

        return userMapper.convertToResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPermissionsResponse getUserPermissions(UUID userId) {
        log.info("Resolving effective permissions for user ID: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        String roleName = userRepository.findRoleNameByUserId(userId).orElse("UNKNOWN");
        List<String> roleAuthorities = userRepository.findRoleAuthoritiesByUserId(userId);

        Set<String> effective = new HashSet<>(roleAuthorities);

        return new UserPermissionsResponse(
                userId,
                user.getEmail(),
                roleName,
                effective
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(int page, int size) {
        log.info("Listing paginated users page: {}, size: {}", page, size);

        Page<AppUser> userPage = userRepository.findAll(PageRequest.of(page, size));
        Page<UserResponse> dtoPage = userPage.map(userMapper::convertToResponse);

        return new PageResponse<>(dtoPage);
    }
}