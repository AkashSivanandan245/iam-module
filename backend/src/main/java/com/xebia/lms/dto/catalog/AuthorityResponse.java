/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.catalog;

import java.util.UUID;

/**
 * Data Transfer Object representing details of an authority combination.
 *
 * @param id unique authority UUID
 * @param authorityName the formatted MODULE:ACTION representation (e.g. USER:CREATE)
 */
public record AuthorityResponse(
    UUID id,
    String authorityName
) {}

