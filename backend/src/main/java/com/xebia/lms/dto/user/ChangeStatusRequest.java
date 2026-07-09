/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import com.xebia.lms.domain.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing a request to modify a user's account status.
 *
 * @param status target {@link UserStatus} to apply
 */
public record ChangeStatusRequest(
    @NotNull(message = "Status cannot be null")
    UserStatus status
) {}

