/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.dto.user.*;
import java.util.UUID;

/**
 * Service interface responsible for user account lifecycle management.
 *
 * Responsibilities:
 * - Onboarding new users and triggering initial default password setups.
 * - Retrieving and updating user profile details.
 * - Managing status transitions (ACTIVE, SUSPENDED, INVITED).
 * - Assigning system roles to users.
 * - Inspecting effective user permissions.
 */
public interface UserService {

    /**
     * Onboards a new user in the system.
     *
     * Business Rules:
     * - Email must be unique.
     * - Default status is INVITED.
     * - Generates a default secure password and hashes it.
     *
     * @param request user creation details
     * @return created user response DTO
     * @throws com.xebia.lms.common.exception.ConflictException if email already exists
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Retrieves user details by their unique identity.
     *
     * @param userId unique user UUID
     * @return matching user details DTO
     * @throws com.xebia.lms.common.exception.NotFoundException if user is not found
     */
    UserResponse getUserById(UUID userId);

    /**
     * Updates an existing user's profile details.
     *
     * @param userId unique user UUID
     * @param request update details (displayName, timezone)
     * @return updated user details DTO
     */
    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    /**
     * Modifies a user account status (ACTIVE, SUSPENDED, INVITED).
     *
     * Business Rules:
     * - If status changes, increments permissionVersion to invalidate cached tokens.
     *
     * @param userId unique user UUID
     * @param request target status change details
     * @return updated user details DTO
     */
    UserResponse changeUserStatus(UUID userId, ChangeStatusRequest request);

    /**
     * Assigns a new role to the specified user.
     *
     * Business Rules:
     * - Increments permissionVersion so the new role immediately updates permissions cache.
     *
     * @param userId unique user UUID
     * @param request target role assignment details
     * @return updated user details DTO
     */
    UserResponse assignUserRole(UUID userId, AssignRoleRequest request);

    /**
     * Inspects and returns the effective permissions (role authorities + overrides) for a user.
     *
     * @param userId unique user UUID
     * @return details of user permissions DTO
     */
    UserPermissionsResponse getUserPermissions(UUID userId);

    /**
     * Lists users using standard pagination parameters.
     *
     * @param page page index (0-indexed)
     * @param size number of records per page
     * @return paginated user list response wrapper
     */
    PageResponse<UserResponse> listUsers(int page, int size);
}

