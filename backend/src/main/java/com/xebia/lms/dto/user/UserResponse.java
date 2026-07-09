/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import com.xebia.lms.domain.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing user details returned by queries.
 *
 * @param userId unique user identity
 * @param email user email address
 * @param displayName user profile name
 * @param roleId assigned role identity
 * @param organisationId organisation identity the user belongs to
 * @param timezone user preferred timezone
 * @param status current account status
 * @param createdAt account creation timestamp
 * @param lastLoginAt user last successful login timestamp
 */
public record UserResponse(
    UUID userId,
    String email,
    String displayName,
    UUID roleId,
    UUID organisationId,
    String timezone,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {}

