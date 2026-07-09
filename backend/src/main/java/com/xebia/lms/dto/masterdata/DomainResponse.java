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
 * Data Transfer Object representing the details of a domain category.
 *
 * @param id unique domain UUID
 * @param name name of the domain
 * @param createdAt timestamp when the domain was registered
 */
public record DomainResponse(
    UUID id,
    String name,
    LocalDateTime createdAt
) {}

