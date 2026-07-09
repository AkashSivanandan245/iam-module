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
 * Data Transfer Object representing the details of an organisation.
 *
 * @param id unique organisation UUID
 * @param name name of the organisation
 * @param createdAt timestamp when the organisation was registered
 */
public record OrganisationResponse(
    UUID id,
    String name,
    LocalDateTime createdAt
) {}

