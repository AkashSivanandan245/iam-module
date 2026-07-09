/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.auth;

import java.util.UUID;

/**
 * Data Transfer Object representing the profile details of the currently authenticated user.
 *
 * @param userId unique user identity
 * @param email user email address
 * @param displayName user display name
 * @param roleId current role identity
 * @param organisationId organisation identity the user belongs to
 * @param timezone user preferred timezone
 * @param status current status of the user (e.g. ACTIVE, SUSPENDED)
 */
public record MeResponse(
    UUID userId,
    String email,
    String displayName,
    UUID roleId,
    UUID organisationId,
    String timezone,
    String status
) {}

