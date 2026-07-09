/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.masterdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object representing a request to update an existing organisation.
 *
 * @param name name of the organisation
 */
public record UpdateOrganisationRequest(
    @NotBlank(message = "Organisation name is required")
    @Size(max = 255, message = "Organisation name cannot exceed 255 characters")
    String name
) {}

