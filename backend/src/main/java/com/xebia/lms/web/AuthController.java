/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.dto.auth.*;
import com.xebia.lms.security.CurrentUser;
import com.xebia.lms.security.LmsPrincipal;
import com.xebia.lms.service.AuthService;
import com.xebia.lms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for managing user authentication endpoints.
 *
 * Exposes login, token refresh, logout, password recovery, and profile mapping APIs.
 * Ensures stateless session control and complies with OpenAPI specifications.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, logout, token refresh, and password recovery flows")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Authenticates user credentials and issues a set of access/refresh JWT tokens.
     *
     * @param request credentials request details
     * @return ResponseEntity containing TokenResponse DTO
     */
    @Operation(summary = "Authenticate user", description = "Verifies email/password and issues access and refresh tokens.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.email());
        TokenResponse response = authService.login(request);
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/auth/refresh")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
    }

    /**
     * Rotates expired access tokens using a valid refresh token.
     *
     * @param request token refresh details containing the refresh token
     * @return ResponseEntity containing TokenResponse DTO
     */
    @Operation(summary = "Refresh access token", description = "Rotates access and refresh tokens using a valid refresh token cookie.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String cookieToken,
            @RequestBody(required = false) RefreshRequest request) {
        log.info("REST request to refresh access token");
        
        String token = cookieToken != null ? cookieToken : (request != null ? request.refreshToken() : null);
        if (token == null || token.isBlank()) {
            throw new com.xebia.lms.common.exception.AuthException("Refresh token is missing");
        }

        TokenResponse response = authService.refresh(token);
        
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/auth/refresh")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(response);
    }

    /**
     * Logouts and invalidates the user session (clears token on client).
     *
     * @return ResponseEntity representing status success
     */
    @Operation(summary = "Logout user", description = "Invalidates the active session (client is expected to discard tokens) and clears cookies.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("REST request to logout active user session");
        authService.logout();
        
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/auth/refresh")
            .maxAge(0)
            .sameSite("Strict")
            .build();
            
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
            .build();
    }

    /**
     * Dispatches a password recovery OTP to the user's email if registered.
     *
     * @param request forgot password details containing target email
     * @return ResponseEntity representing status success
     */
    @Operation(summary = "Forgot password", description = "Generates a password reset OTP and sends it via email if the address exists.")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("REST request to trigger forgot password flow for email: {}", request.email());
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifies if the user-inputted OTP matches the active token.
     *
     * @param request verification details containing email and OTP code
     * @return ResponseEntity containing true if valid, false otherwise
     */
    @Operation(summary = "Verify OTP", description = "Verifies if the submitted OTP is correct and active.")
    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("REST request to verify OTP for email: {}", request.email());
        boolean isValid = authService.verifyOtp(request);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Executes the password reset action using the verified OTP.
     *
     * @param request reset details containing email, OTP code, and new password
     * @return ResponseEntity representing status success
     */
    @Operation(summary = "Reset password", description = "Resets user password using a verified OTP. Increments permissionVersion to force-logout active tokens.")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("REST request to reset password for email: {}", request.email());
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the profile details for the currently authenticated user.
     *
     * @param principal currently logged-in principal details resolved by custom annotation
     * @return ResponseEntity containing MeResponse DTO
     */
    @Operation(summary = "Get current user profile", description = "Retrieves the active user profile details resolved from security context.")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@CurrentUser LmsPrincipal principal) {
        log.info("REST request to fetch profile details for active user ID: {}", principal.getUserId());
        MeResponse response = authService.getCurrentUserProfile(principal);
        return ResponseEntity.ok(response);
    }

    /**
     * Inspects and returns effective permissions resolved for the authenticated user.
     *
     * @param principal currently logged-in principal
     * @return ResponseEntity containing UserPermissionsResponse DTO
     */
    @Operation(summary = "Get my permissions", description = "Inspects and returns resolved authorities for the active user.")
    @GetMapping("/me/permissions")
    public ResponseEntity<com.xebia.lms.dto.user.UserPermissionsResponse> getMyPermissions(@CurrentUser LmsPrincipal principal) {
        log.info("REST request to fetch effective permissions for active user ID: {}", principal.getUserId());
        com.xebia.lms.dto.user.UserPermissionsResponse response = userService.getUserPermissions(principal.getUserId());
        return ResponseEntity.ok(response);
    }
}

