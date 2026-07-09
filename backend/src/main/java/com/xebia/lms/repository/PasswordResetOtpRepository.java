/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.PasswordResetOtp;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link PasswordResetOtp} persistence.
 *
 * Exposes standard CRUD operations and custom query methods for accessing
 * and cleaning up short-lived Password Reset OTP tokens.
 */
@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {

    /**
     * Finds an active password reset OTP code mapping to the requested email and token value.
     *
     * @param email the target user's email address
     * @param otpCode the generated one-time code to match
     * @return an Optional containing the matching OTP entity if found, otherwise empty
     */
    Optional<PasswordResetOtp> findByEmailAndOtpCode(String email, String otpCode);

    /**
     * Deletes all OTP codes associated with a specific user email.
     * Used to invalidate prior codes when generating a new code or completing password reset.
     *
     * @param email the target email address for which OTP codes should be cleared
     */
    void deleteByEmail(String email);
}

