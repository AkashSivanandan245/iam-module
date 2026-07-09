/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.masterdata;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing the details of a branch.
 *
 * @param id unique branch UUID
 * @param name name of the branch
 * @param universityId university UUID the branch belongs to
 * @param createdAt timestamp when the branch was registered
 */
public record BranchResponse(
    UUID id,
    String name,
    UUID universityId,
    LocalDateTime createdAt
) {}

