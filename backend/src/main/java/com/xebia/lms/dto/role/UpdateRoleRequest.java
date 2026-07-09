/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a request to update an existing system role.
 *
 * @param name name of the role (e.g. ADMINISTRATOR)
 * @param description brief description of the role's responsibilities
 */
public record UpdateRoleRequest(
    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name cannot exceed 100 characters")
    String name,

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description
) {}

