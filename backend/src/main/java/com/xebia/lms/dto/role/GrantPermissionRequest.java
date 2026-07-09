/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.role;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to grant a single permission to a role.
 *
 * @param authorityId the UUID of the authority to assign
 */
public record GrantPermissionRequest(
    @NotNull(message = "Permission ID cannot be null")
    UUID permissionId
) {}

