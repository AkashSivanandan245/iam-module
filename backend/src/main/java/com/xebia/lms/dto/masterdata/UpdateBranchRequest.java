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
 * Data Transfer Object representing a request to update an existing branch/department.
 *
 * @param name name of the branch
 * @param universityId university UUID the branch belongs to
 */
public record UpdateBranchRequest(
    @NotBlank(message = "Branch name is required")
    @Size(max = 255, message = "Branch name cannot exceed 255 characters")
    String name,

    @NotNull(message = "University ID is required")
    UUID universityId
) {}

