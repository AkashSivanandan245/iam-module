/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.security.LmsPrincipal;
import io.jsonwebtoken.Claims;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests verifying JWT token generation, parsing, and cryptographic signature verification.
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTests {

    @Autowired
    private JwtService jwtService;

    /**
     * Verifies that a valid access token is generated with correct claims
     * and can be parsed back successfully.
     */
    @Test
    void testGenerateAndParseAccessToken() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        
        AppUser user = AppUser.builder()
            .userId(userId)
            .email("testuser@xebia.com")
            .displayName("Test User")
            .roleId(roleId)
            .orgId(orgId)
            .permissionVersion(5)
            .status(UserStatus.ACTIVE)
            .build();
            
        LmsPrincipal principal = LmsPrincipal.build(user, Collections.emptyList());
        
        String token = jwtService.generateAccessToken(principal);
        assertNotNull(token);
        
        // Validate signature and claims parsing
        assertTrue(jwtService.validateToken(token));
        assertEquals("testuser@xebia.com", jwtService.getUsernameFromToken(token));
        
        Claims claims = jwtService.parseToken(token);
        assertEquals(userId.toString(), claims.get("userId", String.class));
        assertEquals(roleId.toString(), claims.get("roleId", String.class));
        assertEquals(5, claims.get("permissionVersion", Integer.class));
    }

    /**
     * Verifies that validation fails cleanly for malformed or invalid tokens.
     */
    @Test
    void testValidateInvalidToken() {
        String malformedToken = "invalid.token.payload";
        assertFalse(jwtService.validateToken(malformedToken));
    }
}

