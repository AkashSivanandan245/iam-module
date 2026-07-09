/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.auth;

/**
 * Data Transfer Object representing the token response after successful authentication.
 *
 * Contains access and refresh JWT tokens, and token expiration timing details.
 *
 * @param accessToken JWT access token for authorization headers
 * @param refreshToken JWT refresh token for obtaining new access tokens
 * @param expiresIn duration in seconds before the access token expires
 * @param tokenType type of authorization token, typically "Bearer"
 */
public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    String tokenType
) {}

