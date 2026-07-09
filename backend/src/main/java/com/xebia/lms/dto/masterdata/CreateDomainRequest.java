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
 * Data Transfer Object representing a request to register a new domain category.
 *
 * @param name unique name of the domain (e.g. SOFTWARE_ENGINEERING)
 */
public record CreateDomainRequest(
    @NotBlank(message = "Domain name is required")
    @Size(max = 255, message = "Domain name cannot exceed 255 characters")
    String name
) {}

