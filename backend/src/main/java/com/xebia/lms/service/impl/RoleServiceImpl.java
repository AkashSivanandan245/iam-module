/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.Permission;
import com.xebia.lms.domain.RolePermission;
import com.xebia.lms.repository.PermissionRepository;
import com.xebia.lms.service.RoleService;
import com.xebia.lms.service.AuditService;
import com.xebia.lms.domain.OutboxEvent;
import com.xebia.lms.domain.enums.OutboxStatus;
import com.xebia.lms.repository.OutboxEventRepository;
import com.xebia.lms.domain.Role;
import com.xebia.lms.dto.role.CreateRoleRequest;
import com.xebia.lms.dto.role.RoleResponse;
import com.xebia.lms.dto.role.UpdateRolePermissionsRequest;
import com.xebia.lms.dto.role.UpdateRoleRequest;
import com.xebia.lms.mapper.RoleMapper;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing Role business logic.
 *
 * Implements CRUD actions, authority mappings, and versioned invalidation updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AppUserRepository userRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final RoleMapper roleMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        log.info("Attempting to create new role: {}", request.name());

        Optional<Role> existingRole = roleRepository.findByName(request.name());
        if (existingRole.isPresent()) {
            log.warn("Role creation aborted. Role name already exists: {}", request.name());
            throw new ConflictException("Role with name " + request.name() + " already exists");
        }

        Role role = roleMapper.convertToEntity(request);
        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully. ID: {}, Name: {}", savedRole.getRoleId(), savedRole.getName());

        auditService.log(null, "ROLE_CREATED", "Role",
                savedRole.getRoleId().toString(), AuditServiceImpl.resolveClientIp(),
                "{\"name\":\"" + savedRole.getName() + "\"}");

        return roleMapper.convertToResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(UUID id, UpdateRoleRequest request) {
        log.info("Updating role details for ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));

        Optional<Role> existingWithName = roleRepository.findByName(request.name());
        if (existingWithName.isPresent() && !existingWithName.get().getRoleId().equals(id)) {
            log.warn("Role update aborted. Name conflict: {}", request.name());
            throw new ConflictException("Role with name " + request.name() + " already exists");
        }

        role.setName(request.name());
        role.setDescription(request.description());

        Role updatedRole = roleRepository.save(role);
        log.info("Role details successfully updated. ID: {}", id);

        return roleMapper.convertToResponse(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        log.info("Deleting role by ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));

        roleRepository.delete(role);
        log.info("Role successfully deleted. ID: {}", id);

        auditService.log(null, "ROLE_DELETED", "Role",
                id.toString(), AuditServiceImpl.resolveClientIp(),
                "{\"name\":\"" + role.getName() + "\"}");
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Retrieving all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoleResponse updateRolePermissions(UUID id, UpdateRolePermissionsRequest request) {
        log.info("Updating mapped authorities for role ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with ID " + id + " not found"));

        List<Permission> permissions = permissionRepository.findByPermissionIdIn(request.permissionIds());
        role.getRolePermissions().clear();
        for (Permission p : permissions) {
            RolePermission rp = RolePermission.builder()
                    .role(role)
                    .permission(p)
                    .build();
            role.getRolePermissions().add(rp);
        }

        Role savedRole = roleRepository.save(role);

        // Bulk invalidation: increment permissionVersion for all users mapped to this role.
        // This triggers a version mismatch at filter level, forcing Redis cache updates.
        userRepository.incrementPermissionVersionByRoleId(id);

        OutboxEvent event = OutboxEvent.builder()
                .eventType("permission-changed")
                .payload("{\"roleId\":\"" + id + "\"}")
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        log.info("Role authorities updated and user permission versions bulk-incremented. Role ID: {}", id);

        auditService.log(null, "ROLE_PERMISSIONS_UPDATED", "Role",
                id.toString(), AuditServiceImpl.resolveClientIp(),
                "{\"permissionCount\":" + request.permissionIds().size() + "}");

        return roleMapper.convertToResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse grantPermission(UUID roleId, UUID permissionId) {
        log.info("Granting permission {} to role {}", permissionId, roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role with ID " + roleId + " not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission with ID " + permissionId + " not found"));

        boolean exists = role.getRolePermissions().stream()
                .anyMatch(rp -> rp.getPermission().getPermissionId().equals(permissionId));

        if (!exists) {
            RolePermission rp = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            role.getRolePermissions().add(rp);
        }
        Role savedRole = roleRepository.save(role);

        userRepository.incrementPermissionVersionByRoleId(roleId);

        OutboxEvent event = OutboxEvent.builder()
                .eventType("permission-changed")
                .payload("{\"roleId\":\"" + roleId + "\"}")
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        log.info("Permission granted and user permission versions bulk-incremented. Role ID: {}", roleId);

        return roleMapper.convertToResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse revokePermission(UUID roleId, UUID permissionId) {
        log.info("Revoking permission {} from role {}", permissionId, roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role with ID " + roleId + " not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission with ID " + permissionId + " not found"));

        role.getRolePermissions().removeIf(rp -> rp.getPermission().getPermissionId().equals(permissionId));
        Role savedRole = roleRepository.save(role);

        userRepository.incrementPermissionVersionByRoleId(roleId);

        OutboxEvent event = OutboxEvent.builder()
                .eventType("permission-changed")
                .payload("{\"roleId\":\"" + roleId + "\"}")
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        log.info("Permission revoked and user permission versions bulk-incremented. Role ID: {}", roleId);

        return roleMapper.convertToResponse(savedRole);
    }
}
