/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.dto.role.CreateRoleRequest;
import com.xebia.lms.dto.role.GrantPermissionRequest;
import com.xebia.lms.dto.role.RoleResponse;
import com.xebia.lms.dto.role.UpdateRolePermissionsRequest;
import com.xebia.lms.dto.role.UpdateRoleRequest;
import com.xebia.lms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for managing system roles and mappings to authorities.
 *
 * All operations require explicit client pre-authorization.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Endpoints for creating system roles, updating role details, and mapping authorities")
public class RoleController {

    private final RoleService roleService;

    /**
     * Registers a new logical role in the system.
     *
     * @param request creation request containing role name and description
     * @return ResponseEntity holding the created RoleResponse DTO
     */
    @Operation(summary = "Create role", description = "Creates a new system role with unique name.")
    @PreAuthorize("hasAuthority('ROLE:CREATE')")
    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        log.info("REST request to create role: {}", request.name());
        RoleResponse response = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all registered system roles.
     *
     * @return ResponseEntity holding list of RoleResponse DTOs
     */
    @Operation(summary = "List roles", description = "Retrieves all registered logical roles and their assigned permission names.")
    @PreAuthorize("hasAuthority('ROLE:READ')")
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("REST request to list all roles");
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing role name or description details.
     *
     * @param id role unique UUID
     * @param request update details
     * @return ResponseEntity holding the updated RoleResponse DTO
     */
    @Operation(summary = "Update role details", description = "Modifies name and description of an existing role.")
    @PreAuthorize("hasAuthority('ROLE:UPDATE')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> updateRole(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRoleRequest request
    ) {
        log.info("REST request to update role details for ID: {}", id);
        RoleResponse response = roleService.updateRole(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Maps permission authorities to a specific system role.
     * Triggers bulk user version invalidation downstream.
     *
     * @param id role unique UUID
     * @param request target authorities collection mapped to the role
     * @return ResponseEntity holding the updated RoleResponse DTO containing updated authorities
     */
    @Operation(summary = "Assign role permissions", description = "Overwrites mapped authorities assigned to a role. Automatically bulk-invalidates cached permissions for all users assigned to this role.")
    @PreAuthorize("hasAuthority('ROLE:UPDATE')")
    @PatchMapping("/roles/{id}/permissions")
    public ResponseEntity<RoleResponse> updateRolePermissions(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRolePermissionsRequest request
    ) {
        log.info("REST request to update mapped permissions for role ID: {}", id);
        RoleResponse response = roleService.updateRolePermissions(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Grants a single permission to a specific role.
     *
     * @param id role unique UUID
     * @param request target authority ID
     * @return ResponseEntity holding the updated RoleResponse DTO
     */
    @Operation(summary = "Grant permission", description = "Grants a single permission to a role. Automatically bulk-invalidates cached permissions.")
    @PreAuthorize("hasAuthority('ADM:RBAC:MANAGE')")
    @PostMapping("/admin/roles/{id}/permissions")
    public ResponseEntity<RoleResponse> grantPermission(
        @PathVariable UUID id,
        @Valid @RequestBody GrantPermissionRequest request
    ) {
        log.info("REST request to grant permission to role ID: {}", id);
        RoleResponse response = roleService.grantPermission(id, request.permissionId());
        return ResponseEntity.ok(response);
    }

    /**
     * Revokes a single permission from a specific role.
     *
     * @param id role unique UUID
     * @param permId target authority UUID to revoke
     * @return ResponseEntity holding the updated RoleResponse DTO
     */
    @Operation(summary = "Revoke permission", description = "Revokes a single permission from a role. Automatically bulk-invalidates cached permissions.")
    @PreAuthorize("hasAuthority('ADM:RBAC:MANAGE')")
    @DeleteMapping("/admin/roles/{id}/permissions/{permId}")
    public ResponseEntity<RoleResponse> revokePermission(
        @PathVariable UUID id,
        @PathVariable UUID permId
    ) {
        log.info("REST request to revoke permission from role ID: {}", id);
        RoleResponse response = roleService.revokePermission(id, permId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a logical role from the system if no user dependencies exist.
     *
     * @param id role unique UUID
     * @return ResponseEntity indicating success status
     */
    @Operation(summary = "Delete role", description = "Deletes a logical role from the catalog.")
    @PreAuthorize("hasAuthority('ROLE:DELETE')")
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        log.info("REST request to delete role ID: {}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}

