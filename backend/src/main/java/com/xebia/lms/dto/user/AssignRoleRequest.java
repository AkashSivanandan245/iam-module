/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to change a user's role.
 *
 * @param roleId the target role identity to assign
 */
public record AssignRoleRequest(
    @NotNull(message = "Role ID is required")
    UUID roleId
) {}

