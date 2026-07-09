/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.xebia.lms.domain.PasswordResetOtp;
import com.xebia.lms.repository.PasswordResetOtpRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration/Unit tests verifying One-Time Password (OTP) generation, database persistence,
 * validation constraints, and lifecycle cleanup.
 */
@SpringBootTest
@ActiveProfiles("test")
class OtpServiceTests {

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    /**
     * Verifies that generating an OTP creates a valid record in the database,
     * matches correctly, and invalidates previous codes.
     */
    @Test
    void testOtpGenerationAndValidation() {
        String email = "otp-test@xebia.com";
        
        // Generate first OTP
        String firstCode = otpService.generateOtp(email);
        assertNotNull(firstCode);
        assertEquals(6, firstCode.length());
        
        // Validate first code is correct
        assertTrue(otpService.validateOtp(email, firstCode));
        
        // Generate second OTP for same email
        String secondCode = otpService.generateOtp(email);
        assertNotNull(secondCode);
        assertNotEquals(firstCode, secondCode);
        
        // Old code must be invalidated/deleted automatically
        assertFalse(otpService.validateOtp(email, firstCode));
        
        // New code must be active
        assertTrue(otpService.validateOtp(email, secondCode));
        
        // Incorrect code validation must fail
        assertFalse(otpService.validateOtp(email, "000000"));
    }

    /**
     * Verifies that clearing OTPs deletes all active tokens associated with the email address.
     */
    @Test
    void testClearOtp() {
        String email = "clear-test@xebia.com";
        String code = otpService.generateOtp(email);
        
        assertTrue(otpService.validateOtp(email, code));
        
        // Clear active tokens
        otpService.clearOtp(email);
        
        // Validation should now fail since it was deleted
        assertFalse(otpService.validateOtp(email, code));
    }
}

