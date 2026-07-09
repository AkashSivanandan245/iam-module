/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.catalog;

import java.util.UUID;

/**
 * Data Transfer Object representing the details of an operation action.
 *
 * @param id unique action UUID
 * @param name unique name of the action
 * @param description action description
 */
public record ActionResponse(
    UUID id,
    String name,
    String description
) {}

