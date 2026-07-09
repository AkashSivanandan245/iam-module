/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.ratelimit;

import com.xebia.lms.common.exception.TooManyRequestsException;
import com.xebia.lms.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that enforces configured rate-limit rules against incoming requests.
 *
 * Positioned before JwtAuthenticationFilter in the security chain so throttling happens
 * before any expensive auth work. Client identity is derived from the remote IP address,
 * with support for X-Forwarded-For headers when running behind a proxy.
 *
 * Only the first matching rule for a given request is applied — order rules from
 * most specific to least specific in application.yml.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppProperties appProperties;
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        AppProperties.RateLimit config = appProperties.getRateLimit();
        if (!config.isEnabled() || config.getRules().isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        AppProperties.RateLimit.Rule matched = findMatchingRule(request.getRequestURI(), config.getRules());
        if (matched == null) {
            chain.doFilter(request, response);
            return;
        }

        String clientId = resolveClientId(request);
        RateLimitOutcome outcome = rateLimitService.hit(matched, clientId);

        // Advertise limits back to well-behaved clients — same convention Github/Stripe/etc. use.
        response.setHeader("X-RateLimit-Limit", String.valueOf(outcome.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, outcome.limit() - outcome.currentCount())));

        if (!outcome.allowed()) {
            log.warn("Rate limit hit. rule={} client={} count={} limit={}",
                    matched.getName(), clientId, outcome.currentCount(), outcome.limit());
            throw new TooManyRequestsException(
                    "Too many requests for " + matched.getName() + ". Please retry after " + outcome.retryAfterSeconds() + " seconds.",
                    outcome.retryAfterSeconds()
            );
        }

        chain.doFilter(request, response);
    }

    /**
     * Returns the first rule whose pathPattern matches the request URI, or null when none do.
     */
    private AppProperties.RateLimit.Rule findMatchingRule(String uri, List<AppProperties.RateLimit.Rule> rules) {
        for (AppProperties.RateLimit.Rule rule : rules) {
            if (PATH_MATCHER.match(rule.getPathPattern(), uri)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * Derives a stable client identity for keying the rate-limit counter.
     * Prefers the first entry of X-Forwarded-For when running behind a reverse proxy.
     */
    private String resolveClientId(HttpServletRequest request) {
        String forwarded = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwarded != null && !forwarded.isBlank()) {
            // XFF can be a comma-separated list — the first entry is the originating client.
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
