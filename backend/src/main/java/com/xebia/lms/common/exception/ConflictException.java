/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common.exception;

/**
 * Custom exception representing a resource state conflict condition (e.g. duplicate email).
 * Maps to HTTP Status 409 (Conflict).
 */
public class ConflictException extends RuntimeException {
    
    /**
     * Constructs a new ConflictException with the specified detail message.
     *
     * @param message the detail message explaining the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}

