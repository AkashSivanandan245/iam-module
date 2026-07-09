/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.jwt;

import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.security.AuthorityResolver;
import com.xebia.lms.security.LmsPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that intercepts incoming HTTP requests to validate JWT access tokens.
 *
 * If a valid Bearer token is found in the Authorization header:
 * - Validates the token against the RSA public key.
 * - Extracts identity claims (userId, roleId, permissionVersion).
 * - Resolves effective user permissions using cache-backed resolver.
 * - Populates Spring Security's {@link SecurityContextHolder} with an {@link LmsPrincipal}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final AuthorityResolver authorityResolver;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = parseBearerToken(request);
            
            if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
                Claims claims = jwtService.parseToken(jwt);
                UUID userId = UUID.fromString(claims.get("userId", String.class));
                int permissionVersion = claims.get("permissionVersion", Integer.class);
                
                // Fetch the user from the database to check status
                appUserRepository.findById(userId).ifPresent(user -> {
                    if (user.getStatus() != UserStatus.SUSPENDED) {
                        List<SimpleGrantedAuthority> authorities = authorityResolver.resolveAuthorities(userId, permissionVersion);
                        
                        LmsPrincipal principal = LmsPrincipal.build(user, authorities);
                        
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Successfully authenticated user: {}", user.getEmail());
                    } else {
                        log.warn("Blocked authentication attempt for suspended user: {}", user.getEmail());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            // We do not fail the request here; standard Spring Security filter chain 
            // will block unauthorized requests for secured endpoints automatically.
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the Bearer token value from the Authorization header.
     *
     * @param request the incoming HTTP servlet request
     * @return the token string if found, otherwise null
     */
    private String parseBearerToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}

