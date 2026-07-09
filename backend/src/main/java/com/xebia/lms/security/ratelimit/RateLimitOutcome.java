/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security.ratelimit;

/**
 * Immutable value describing the result of a single rate-limit check.
 *
 * @param allowed           true when the caller is still within budget
 * @param currentCount      the caller's count in the active window
 * @param limit             the configured maximum allowed inside the window
 * @param retryAfterSeconds seconds remaining until the current window resets
 */
public record RateLimitOutcome(
        boolean allowed,
        long currentCount,
        long limit,
        long retryAfterSeconds
) {

    public static RateLimitOutcome allowed(long currentCount, long limit, long retryAfterSeconds) {
        return new RateLimitOutcome(true, currentCount, limit, retryAfterSeconds);
    }

    public static RateLimitOutcome denied(long currentCount, long limit, long retryAfterSeconds) {
        return new RateLimitOutcome(false, currentCount, limit, retryAfterSeconds);
    }
}
