/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.role;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to modify authorities mapped to a role.
 */
public record UpdateRolePermissionsRequest(
    @NotNull(message = "Permission IDs set cannot be null")
    Set<UUID> permissionIds
) {}

