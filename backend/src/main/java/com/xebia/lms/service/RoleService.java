/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.role.CreateRoleRequest;
import com.xebia.lms.dto.role.RoleResponse;
import com.xebia.lms.dto.role.UpdateRolePermissionsRequest;
import com.xebia.lms.dto.role.UpdateRoleRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Role Management.
 *
 * Responsibilities:
 * - Role CRUD operations.
 * - Mapping system authorities to roles.
 * - Dynamic permission version cache invalidation on mappings adjustment.
 */
public interface RoleService {

    /**
     * Creates a new role in the system.
     *
     * @param request creation details
     * @return the created role response details
     */
    RoleResponse createRole(CreateRoleRequest request);

    /**
     * Updates details of an existing role.
     *
     * @param id role unique UUID
     * @param request update details
     * @return the updated role details
     */
    RoleResponse updateRole(UUID id, UpdateRoleRequest request);

    /**
     * Deletes a role from the system.
     *
     * @param id role unique UUID
     */
    void deleteRole(UUID id);

    /**
     * Lists all system roles.
     *
     * @return list of roles DTOs
     */
    List<RoleResponse> getAllRoles();

    /**
     * Updates mapped permissions/authorities for a role.
     *
     * Business Rules:
     * - Increments permission version for all users belonging to this role, forcing
     *   Redis cache token invalidation.
     *
     * @param id role unique UUID
     * @param request mapping details holding target authority UUIDs
     * @return updated role details DTO
     */
    RoleResponse updateRolePermissions(UUID id, UpdateRolePermissionsRequest request);

    /**
     * Grants a single permission to a role.
     */
    RoleResponse grantPermission(UUID roleId, UUID permissionId);

    /**
     * Revokes a single permission from a role.
     */
    RoleResponse revokePermission(UUID roleId, UUID permissionId);
}

