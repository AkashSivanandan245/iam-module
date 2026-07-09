/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common.exception;

/**
 * Custom exception representing authentication or authorization failures.
 * Maps to HTTP Status 401 (Unauthorized) or 403 (Forbidden).
 */
public class AuthException extends RuntimeException {
    
    /**
     * Constructs a new AuthException with the specified detail message.
     *
     * @param message the authentication or authorization failure message
     */
    public AuthException(String message) {
        super(message);
    }
}

