/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.ratelimit;

import com.xebia.lms.config.AppProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service that enforces per-client rate limits using a fixed-window Redis counter.
 *
 * Algorithm:
 * - Key format: rl:{ruleName}:{clientId}:w{floor(now / windowSeconds)}
 * - On each hit: INCR the counter and, if it's the first hit of the window, EXPIRE it.
 * - If the counter exceeds the configured limit, the caller is over budget.
 *
 * Resiliency:
 * - Any Redis failure results in fail-open behaviour: log the error and let the request through.
 *   This mirrors AuthorityResolver — availability is preferred over hard failure on infra glitches.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String KEY_PREFIX = "rl:";

    private final StringRedisTemplate redisTemplate;

    /**
     * Registers a hit against the given rule for the given client identity.
     *
     * @param rule     the rate-limit rule to check against
     * @param clientId a stable identity for the caller (typically remote IP or userId)
     * @return an outcome carrying the current count, the limit, and seconds until the window resets
     */
    public RateLimitOutcome hit(AppProperties.RateLimit.Rule rule, String clientId) {
        long windowIndex = System.currentTimeMillis() / (rule.getWindowSeconds() * 1000L);
        String key = KEY_PREFIX + rule.getName() + ":" + clientId + ":w" + windowIndex;

        try {
            Long current = redisTemplate.opsForValue().increment(key);
            if (current == null) {
                // Redis returned no value — treat as fail-open, mirroring AuthorityResolver.
                log.warn("Rate-limit counter returned null for key {}. Failing open.", key);
                return RateLimitOutcome.allowed(0, rule.getLimit(), rule.getWindowSeconds());
            }

            // Set the TTL only on the first hit of the window — avoids resetting the expiry on every INCR.
            if (current == 1L) {
                redisTemplate.expire(key, Duration.ofSeconds(rule.getWindowSeconds()));
            }

            long retryAfter = computeRetryAfter(rule.getWindowSeconds(), windowIndex);
            if (current > rule.getLimit()) {
                return RateLimitOutcome.denied(current, rule.getLimit(), retryAfter);
            }
            return RateLimitOutcome.allowed(current, rule.getLimit(), retryAfter);
        } catch (Exception ex) {
            // Fail-open: never block traffic because Redis is having a bad day.
            log.error("Rate-limit check failed for rule={} client={}. Failing open.", rule.getName(), clientId, ex);
            return RateLimitOutcome.allowed(0, rule.getLimit(), rule.getWindowSeconds());
        }
    }

    /**
     * Seconds remaining until the current fixed window rolls over.
     */
    private long computeRetryAfter(int windowSeconds, long windowIndex) {
        long windowStartMs = windowIndex * windowSeconds * 1000L;
        long windowEndMs = windowStartMs + (windowSeconds * 1000L);
        long remainingMs = windowEndMs - System.currentTimeMillis();
        return Math.max(1L, remainingMs / 1000L);
    }
}
