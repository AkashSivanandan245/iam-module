/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.auth.*;
import com.xebia.lms.security.LmsPrincipal;

/**
 * Service interface responsible for authentication workflows.
 *
 * Responsibilities:
 * - Authenticating email/password credentials and generating access/refresh tokens.
 * - Rotating expired access tokens via valid refresh tokens.
 * - Processing password reset flows utilizing OTP tokens and email notifications.
 * - Resolving the currently logged-in user profile response mapping.
 */
public interface AuthService {

    /**
     * Authenticates user credentials and issues a set of access/refresh tokens.
     *
     * @param request credentials request details
     * @return TokenResponse DTO containing JWT tokens
     * @throws com.xebia.lms.common.exception.AuthException if credentials are invalid or account is suspended
     */
    TokenResponse login(LoginRequest request);

    /**
     * Issues new access/refresh tokens in exchange for a valid refresh token.
     *
     * @param refreshToken valid refresh token string
     * @return TokenResponse DTO containing updated JWT tokens
     * @throws com.xebia.lms.common.exception.AuthException if the refresh token is expired or invalid
     */
    TokenResponse refresh(String refreshToken);

    /**
     * Invalidates the active authentication session.
     */
    void logout();

    /**
     * Generates a password reset OTP and triggers an email notification.
     *
     * @param request target email address request details
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Verifies that the input OTP matches the active token generated for the email.
     *
     * @param request verification request details
     * @return true if OTP is valid and active, false otherwise
     */
    boolean verifyOtp(VerifyOtpRequest request);

    /**
     * Resets the user's password to the new input value, provided the OTP is valid.
     *
     * Business Rules:
     * - Invalidates the utilized OTP code.
     * - Increments the user's permissionVersion to force-invalidate all active JWTs.
     *
     * @param request reset password request details
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Resolves the profile details mapping for the currently logged-in principal.
     *
     * @param principal active authenticated LmsPrincipal
     * @return mapped MeResponse DTO details
     */
    MeResponse getCurrentUserProfile(LmsPrincipal principal);
}

