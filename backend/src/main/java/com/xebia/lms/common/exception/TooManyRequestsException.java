/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common.exception;

/**
 * Custom exception representing a rate-limit breach on a protected endpoint.
 * Maps to HTTP Status 429 (Too Many Requests).
 */
public class TooManyRequestsException extends RuntimeException {

    private final long retryAfterSeconds;

    /**
     * Constructs a new TooManyRequestsException with a hint for how long the
     * caller should wait before retrying.
     *
     * @param message           the detail message describing the limit that was hit
     * @param retryAfterSeconds seconds until the current window resets
     */
    public TooManyRequestsException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
