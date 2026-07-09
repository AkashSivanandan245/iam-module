/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.config.AppProperties;
import com.xebia.lms.common.exception.AuthException;
import com.xebia.lms.common.exception.BadRequestException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.dto.auth.*;
import com.xebia.lms.mapper.UserMapper;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.security.LmsPrincipal;
import com.xebia.lms.security.jwt.JwtService;
import com.xebia.lms.service.AuditService;
import com.xebia.lms.service.AuthService;
import com.xebia.lms.service.EmailSender;
import com.xebia.lms.service.OtpService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.time.LocalDateTime;
import com.xebia.lms.repository.RefreshTokenRepository;
import com.xebia.lms.domain.RefreshToken;

/**
 * Service implementation managing user authentication workflows.
 *
 * Implements login verification, token rotation, password reset flows,
 * and user profile resolution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final OtpService otpService;
    private final EmailSender emailSender;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditService auditService;

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}", request.email());

        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed: password mismatch for user {}", request.email());
            // Audit trail: record the failed login attempt.
            auditService.log(user.getUserId(), "LOGIN_FAILED", "AppUser",
                    user.getUserId().toString(), AuditServiceImpl.resolveClientIp(),
                    "{\"email\":\"" + user.getEmail() + "\",\"reason\":\"password_mismatch\"}");
            throw new AuthException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            log.warn("Login blocked: user account {} is suspended", request.email());
            throw new AuthException("Account is suspended");
        }

        List<SimpleGrantedAuthority> authorities = resolveAuthorities(user.getUserId());
        LmsPrincipal principal = LmsPrincipal.build(user, authorities);

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        long expirySeconds = appProperties.getSecurity().getJwt().getAccessTokenExpirationMinutes() * 60L;

        long expiryDays = appProperties.getSecurity().getJwt().getRefreshTokenExpirationDays();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(rt);

        log.info("User {} successfully logged in. Token issued.", user.getEmail());

        // Audit trail: record the successful login event.
        auditService.log(user.getUserId(), "LOGIN_SUCCESS", "AppUser",
                user.getUserId().toString(), AuditServiceImpl.resolveClientIp(),
                "{\"email\":\"" + user.getEmail() + "\"}");

        return new TokenResponse(accessToken, refreshToken, expirySeconds, "Bearer");
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        log.info("Processing token refresh request");

        if (!jwtService.validateToken(refreshToken)) {
            log.warn("Token refresh failed: invalid or expired refresh token");
            throw new AuthException("Invalid refresh token");
        }

        RefreshToken rtRecord = refreshTokenRepository.findByTokenHash(hashToken(refreshToken))
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (rtRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("Invalid refresh token");
        }

        String email = jwtService.getUsernameFromToken(refreshToken);
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User associated with refresh token not found"));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            log.warn("Token refresh blocked: user account {} is suspended", email);
            throw new AuthException("Account is suspended");
        }

        List<SimpleGrantedAuthority> authorities = resolveAuthorities(user.getUserId());
        LmsPrincipal principal = LmsPrincipal.build(user, authorities);

        String newAccessToken = jwtService.generateAccessToken(principal);
        String newRefreshToken = jwtService.generateRefreshToken(principal);

        long expirySeconds = appProperties.getSecurity().getJwt().getAccessTokenExpirationMinutes() * 60L;

        refreshTokenRepository.delete(rtRecord);

        long expiryDays = appProperties.getSecurity().getJwt().getRefreshTokenExpirationDays();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);
        RefreshToken newRt = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(newRefreshToken))
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(newRt);

        log.info("Tokens successfully rotated for user: {}", email);
        return new TokenResponse(newAccessToken, newRefreshToken, expirySeconds, "Bearer");
    }

    @Override
    @Transactional
    public void logout() {
        log.info("Processing user logout.");
        // Get the current user from the SecurityContext
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LmsPrincipal principal) {
            refreshTokenRepository.deleteByUser_UserId(principal.getUserId());
            auditService.log(principal.getUserId(), "LOGOUT", "AppUser",
                    principal.getUserId().toString(), AuditServiceImpl.resolveClientIp(), null);
            log.info("Session invalidated (stateful refresh tokens removed).");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email: {}", request.email());

        // Attempting to prevent user enumeration attacks by returning success
        // even if the email does not exist, but only generating OTP for valid records.
        userRepository.findByEmail(request.email()).ifPresentOrElse(
                user -> {
                    String code = otpService.generateOtp(user.getEmail());
                    emailSender.sendEmail(
                            user.getEmail(),
                            "Password Reset OTP",
                            "A password reset request was initiated. Your 6-digit OTP code is: " + code
                    );
                    log.info("OTP generated and email notification dispatched to: {}", user.getEmail());
                },
                () -> log.warn("Forgot password requested for non-existing email: {}", request.email())
        );
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        log.info("Processing OTP verification check for: {}", request.email());
        return otpService.validateOtp(request.email(), request.otpCode());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset execution for: {}", request.email());

        boolean isOtpValid = otpService.validateOtp(request.email(), request.otpCode());
        if (!isOtpValid) {
            log.warn("Password reset aborted. Invalid or expired OTP code for: {}", request.email());
            throw new BadRequestException("Invalid or expired OTP");
        }

        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        // Permission version is incremented so that
        // Redis cache entries become immediately invalid.
        user.setPermissionVersion(user.getPermissionVersion() + 1);

        userRepository.save(user);
        otpService.clearOtp(request.email());

        log.info("Password successfully reset. Invalidated all active tokens by incrementing version to {}", user.getPermissionVersion());
        auditService.log(user.getUserId(), "PASSWORD_RESET", "AppUser",
                user.getUserId().toString(), AuditServiceImpl.resolveClientIp(),
                "{\"email\":\"" + user.getEmail() + "\"}");
    }

    @Override
    @Transactional(readOnly = true)
    public MeResponse getCurrentUserProfile(LmsPrincipal principal) {
        log.info("Fetching profile details for principal ID: {}", principal.getUserId());

        AppUser user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.convertToMeResponse(user);
    }

    /**
     * Resolves the user's effective permissions based on role authorities and overrides.
     * Match logic in JwtAuthenticationFilter.
     *
     * @param userId the user's UUID
     * @return a list of SimpleGrantedAuthority
     */
    private List<SimpleGrantedAuthority> resolveAuthorities(UUID userId) {
        List<String> roleAuthorities = userRepository.findRoleAuthoritiesByUserId(userId);

        Set<String> effectivePermissions = new HashSet<>(roleAuthorities);

        return effectivePermissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}