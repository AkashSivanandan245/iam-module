/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.masterdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to update an existing university.
 *
 * @param name name of the university
 * @param organisationId organisation UUID the university belongs to
 */
public record UpdateUniversityRequest(
    @NotBlank(message = "University name is required")
    @Size(max = 255, message = "University name cannot exceed 255 characters")
    String name,

    @NotNull(message = "Organisation ID is required")
    UUID organisationId
) {}

