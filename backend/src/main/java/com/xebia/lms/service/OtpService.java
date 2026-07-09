/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

/**
 * Service interface for generating and validating One-Time Passwords (OTP).
 *
 * Utilized during forgot password workflows to verify email identities securely.
 */
public interface OtpService {

    /**
     * Generates a new 6-digit numerical OTP and associates it with the user email.
     * Invalidates any prior OTPs generated for the same email.
     *
     * @param email user email address
     * @return the generated 6-digit OTP code string
     */
    String generateOtp(String email);

    /**
     * Validates that the provided OTP code matches the stored active token and has not expired.
     *
     * @param email user email address
     * @param otpCode the OTP code to verify
     * @return true if valid, false if matched but expired, or mismatch
     */
    boolean validateOtp(String email, String otpCode);

    /**
     * Deletes and invalidates all active OTPs associated with the specified email.
     *
     * @param email target email address
     */
    void clearOtp(String email);
}

