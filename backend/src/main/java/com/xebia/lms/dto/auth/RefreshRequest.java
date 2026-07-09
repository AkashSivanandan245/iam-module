/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object representing a request to rotate an access token.
 *
 * @param refreshToken the active refresh token string
 */
public record RefreshRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}

