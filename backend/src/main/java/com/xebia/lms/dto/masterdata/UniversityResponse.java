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
 * Data Transfer Object representing the details of a university.
 *
 * @param id unique university UUID
 * @param name name of the university
 * @param organisationId organisation UUID the university belongs to
 * @param createdAt timestamp when the university was registered
 */
public record UniversityResponse(
    UUID id,
    String name,
    UUID organisationId,
    LocalDateTime createdAt
) {}

