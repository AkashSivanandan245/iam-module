/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a request to register a new application module.
 *
 * @param name name of the module (e.g. IAM, COURSES)
 * @param description brief description of the module responsibilities
 */
public record CreateModuleRequest(
    @NotBlank(message = "Module key is required")
    @Size(max = 64, message = "Module key cannot exceed 64 characters")
    String key,

    @Size(max = 120, message = "Title cannot exceed 120 characters")
    String title,

    @Size(max = 64, message = "Icon cannot exceed 64 characters")
    String icon,

    @Size(max = 160, message = "Route cannot exceed 160 characters")
    String route
) {}

