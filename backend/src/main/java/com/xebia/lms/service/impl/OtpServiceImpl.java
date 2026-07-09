/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.config.AppProperties;
import com.xebia.lms.domain.PasswordResetOtp;
import com.xebia.lms.repository.PasswordResetOtpRepository;
import com.xebia.lms.service.OtpService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link OtpService} utilizing {@link SecureRandom} for secure token generation
 * and database storage for lifecycle persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final PasswordResetOtpRepository otpRepository;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String generateOtp(String email) {
        log.info("Generating password reset OTP for email: {}", email);
        
        // Invalidate prior OTP codes generated for this email to prevent multiple valid keys
        otpRepository.deleteByEmail(email);
        
        // Generate a cryptographically secure 6-digit number
        int number = 100000 + secureRandom.nextInt(900000);
        String code = String.valueOf(number);
        
        int expiryMinutes = appProperties.getSecurity().getOtp().getExpirationMinutes();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);
        
        PasswordResetOtp otp = PasswordResetOtp.builder()
            .email(email)
            .otpCode(code)
            .expiresAt(expiresAt)
            .build();
            
        otpRepository.save(otp);
        log.debug("OTP generated and stored. Valid for {} minutes.", expiryMinutes);
        
        return code;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOtp(String email, String otpCode) {
        log.info("Validating OTP for email: {}", email);
        
        return otpRepository.findByEmailAndOtpCode(email, otpCode)
            .map(otp -> {
                boolean isNotExpired = otp.getExpiresAt().isAfter(LocalDateTime.now());
                if (!isNotExpired) {
                    log.warn("OTP for email {} matched but has expired", email);
                }
                return isNotExpired;
            })
            .orElseGet(() -> {
                log.warn("No matching OTP found for email {}", email);
                return false;
            });
    }

    @Override
    @Transactional
    public void clearOtp(String email) {
        log.info("Clearing active OTP entries for email: {}", email);
        otpRepository.deleteByEmail(email);
    }
}

