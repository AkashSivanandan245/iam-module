/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.xebia.lms.common.exception.AuthException;
import com.xebia.lms.common.exception.BadRequestException;
import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.dto.auth.*;
import com.xebia.lms.dto.user.CreateUserRequest;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.security.LmsPrincipal;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration/Unit tests verifying authentication workflows, token rotations,
 * forgot password verification codes, and password reset cache updates.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    /**
     * Verifies that standard login succeeds with correct credentials,
     * fails for invalid ones, and rejects suspended account requests.
     */
    @Test
    void testUserLoginWorkflow() {
        UUID roleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String email = "login-" + UUID.randomUUID() + "@xebia.com";
        String rawPassword = "MySecurePassword123!";
        
        AppUser user = AppUser.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .displayName("Login User")
            .roleId(roleId)
            .status(UserStatus.ACTIVE)
            .permissionVersion(1)
            .build();
        userRepository.save(user);

        // Successful Login
        LoginRequest validRequest = new LoginRequest(email, rawPassword);
        TokenResponse response = authService.login(validRequest);
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());

        // Failed Login (invalid password)
        LoginRequest invalidPasswordRequest = new LoginRequest(email, "WrongPassword");
        assertThrows(AuthException.class, () -> authService.login(invalidPasswordRequest));

        // Failed Login (suspended user)
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
        assertThrows(AuthException.class, () -> authService.login(validRequest));
    }

    /**
     * Verifies that the token refresh endpoint rotates tokens successfully.
     */
    @Test
    void testTokenRotation() {
        UUID roleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String email = "refresh-" + UUID.randomUUID() + "@xebia.com";
        String rawPassword = "RefreshPassword123!";
        
        AppUser user = AppUser.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .displayName("Refresh User")
            .roleId(roleId)
            .status(UserStatus.ACTIVE)
            .permissionVersion(1)
            .build();
        userRepository.save(user);

        TokenResponse initialTokens = authService.login(new LoginRequest(email, rawPassword));
        
        // Rotate tokens using refresh token
        TokenResponse rotatedTokens = authService.refresh(initialTokens.refreshToken());
        assertNotNull(rotatedTokens);
        assertNotNull(rotatedTokens.accessToken());
        assertNotEquals(initialTokens.accessToken(), rotatedTokens.accessToken());
    }

    /**
     * Verifies forgot password OTP dispatch, OTP verification, and reset password workflow.
     */
    @Test
    void testForgotPasswordAndResetWorkflow() {
        UUID roleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String email = "forgot-" + UUID.randomUUID() + "@xebia.com";
        String originalPassword = "OriginalPassword123!";
        
        AppUser user = AppUser.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(originalPassword))
            .displayName("Reset User")
            .roleId(roleId)
            .status(UserStatus.ACTIVE)
            .permissionVersion(1)
            .build();
        AppUser savedUser = userRepository.save(user);

        // Dispatches OTP
        authService.forgotPassword(new ForgotPasswordRequest(email));
        
        // Generate an active OTP code for this email manually to simulate email extraction
        String code = otpService.generateOtp(email);
        
        // Verify OTP matches
        assertTrue(authService.verifyOtp(new VerifyOtpRequest(email, code)));

        // Reset password with code
        String newPassword = "NewPassword123!";
        ResetPasswordRequest resetRequest = new ResetPasswordRequest(email, code, newPassword);
        authService.resetPassword(resetRequest);
        
        // Verify user can now log in with the new password
        TokenResponse loginResponse = authService.login(new LoginRequest(email, newPassword));
        assertNotNull(loginResponse);
        
        // Invalidate OTP verification again (must fail since OTP is cleared)
        assertThrows(BadRequestException.class, () -> authService.resetPassword(resetRequest));
    }
}

