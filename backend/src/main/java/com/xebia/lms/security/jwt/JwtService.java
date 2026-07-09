/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.jwt;

import com.xebia.lms.config.AppProperties;
import com.xebia.lms.security.LmsPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing JSON Web Tokens (JWT).
 *
 * Utilizes the RS256 algorithm (RSA Signature with SHA-256) to sign and verify access
 * and refresh tokens. Exposes methods to generate tokens, validate them, and parse
 * security claims.
 *
 * Required Claims:
 * - userId
 * - roleId
 * - permissionVersion
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtKeyProvider keyProvider;
    private final AppProperties appProperties;

    /**
     * Generates a signed access token for the given authenticated principal.
     *
     * Business Rules:
     * - Uses RS256 algorithm signed with the private key.
     * - Contains claims: userId, roleId, permissionVersion.
     * - Expiration is configured via AppProperties (default: 15 minutes).
     *
     * @param principal the authenticated user principal details
     * @return the generated compact serialized JWT token string
     */
    public String generateAccessToken(LmsPrincipal principal) {
        AppProperties.Security.Jwt jwtProps = appProperties.getSecurity().getJwt();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (jwtProps.getAccessTokenExpirationMinutes() * 60L * 1000L));

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(principal.getUsername())
            .claim("userId", principal.getUserId().toString())
            .claim("roleId", principal.getRoleId().toString())
            .claim("permissionVersion", principal.getPermissionVersion())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(keyProvider.getPrivateKey(), Jwts.SIG.RS256)
            .compact();
    }

    /**
     * Generates a signed refresh token for the given authenticated principal.
     *
     * Business Rules:
     * - Uses RS256 algorithm signed with the private key.
     * - Long-lived token configuration (default: 7 days).
     * - Used to rotate access tokens without forcing re-authentication.
     *
     * @param principal the authenticated user principal details
     * @return the generated compact serialized JWT token string
     */
    public String generateRefreshToken(LmsPrincipal principal) {
        AppProperties.Security.Jwt jwtProps = appProperties.getSecurity().getJwt();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (jwtProps.getRefreshTokenExpirationDays() * 24L * 60L * 60L * 1000L));

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(principal.getUsername())
            .claim("userId", principal.getUserId().toString())
            .claim("roleId", principal.getRoleId().toString())
            .claim("permissionVersion", principal.getPermissionVersion())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(keyProvider.getPrivateKey(), Jwts.SIG.RS256)
            .compact();
    }

    /**
     * Parses the compact JWT string into a claims payload.
     * Verified against the public key.
     *
     * @param token compact JWT serialized representation
     * @return parsed Claims payload
     * @throws JwtException if verification, signature, or token validation fails
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(keyProvider.getPublicKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Extracts the subject (email) claim from the token.
     *
     * @param token compact JWT representation
     * @return the email subject
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * Validates that the token signature is correct and has not expired.
     *
     * @param token compact JWT representation
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // Ensure token is not expired (JJWT parseSignedClaims throws ExpiredJwtException,
            // but we perform a safe double-check here).
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token signature or token expired: {}", e.getMessage());
            return false;
        }
    }
}

