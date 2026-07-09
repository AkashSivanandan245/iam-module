/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.dto.user.*;
import com.xebia.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



/**
 * Controller responsible for managing user accounts and authorization details.
 *
 * Exposes endpoints for user CRUD, status changes, role assignments, and permission inspections.
 * Secured via method-level pre-authorization rules based on authorities.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user CRUD, role assignment, status adjustments, and permission inspections")
public class UserController {

    private final UserService userService;

    /**
     * Onboards a new user in the platform.
     *
     * @param request creation request details containing initial profile settings
     * @return ResponseEntity containing UserResponse DTO
     */
    @Operation(summary = "Create user", description = "Onboards a new user with status INVITED and default hashed password.")
    @PreAuthorize("hasAuthority('USER:CREATE')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to create user: {}", request.email());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists users using standard page query parameters.
     *
     * @param page page number (0-indexed)
     * @param size page size (defaults to 10)
     * @return ResponseEntity wrapping paginated UserResponse list
     */
    @Operation(summary = "List users", description = "Returns a paginated list of all users.")
    @PreAuthorize("hasAuthority('USER:READ')")
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.info("REST request to list users. Page: {}, Size: {}", page, size);
        PageResponse<UserResponse> response = userService.listUsers(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves specific user details by ID.
     *
     * @param id user UUID
     * @return ResponseEntity containing UserResponse DTO
     */
    @Operation(summary = "Get user details", description = "Retrieves profile details of a user by UUID.")
    @PreAuthorize("hasAuthority('USER:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("REST request to fetch user ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing user's profile info (displayName, timezone).
     *
     * @param id user UUID
     * @param request update details
     * @return ResponseEntity containing updated UserResponse DTO
     */
    @Operation(summary = "Update user details", description = "Modifies profile settings of a user.")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("REST request to update user ID: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Modifies a user's account status (ACTIVE, SUSPENDED).
     *
     * @param id user UUID
     * @param request target status change details
     * @return ResponseEntity containing updated UserResponse DTO
     */
    @Operation(summary = "Change user status", description = "Suspends or activates a user account. Increments permissionVersion.")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(
        @PathVariable UUID id,
        @Valid @RequestBody ChangeStatusRequest request
    ) {
        log.info("REST request to change status for user ID: {} to: {}", id, request.status());
        UserResponse response = userService.changeUserStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Assigns a new role to the user.
     *
     * @param id user UUID
     * @param request target role ID details
     * @return ResponseEntity containing updated UserResponse DTO
     */
    @Operation(summary = "Assign user role", description = "Assigns a new role to a user. Increments permissionVersion.")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> assignRole(
        @PathVariable UUID id,
        @Valid @RequestBody AssignRoleRequest request
    ) {
        log.info("REST request to change role for user ID: {} to: {}", id, request.roleId());
        UserResponse response = userService.assignUserRole(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Inspects and returns effective permissions resolved for a user.
     *
     * @param id user UUID
     * @return ResponseEntity containing UserPermissionsResponse DTO
     */
    @Operation(summary = "Get user effective permissions", description = "Inspects and returns resolved authorities for a user (role authorities).")
    @PreAuthorize("hasAuthority('USER:READ')")
    @GetMapping("/{id}/permissions")
    public ResponseEntity<UserPermissionsResponse> getUserPermissions(@PathVariable UUID id) {
        log.info("REST request to inspect permissions for user ID: {}", id);
        UserPermissionsResponse response = userService.getUserPermissions(id);
        return ResponseEntity.ok(response);
    }
}

