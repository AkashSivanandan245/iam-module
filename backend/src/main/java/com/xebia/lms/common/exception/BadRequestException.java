/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common.exception;

/**
 * Custom exception representing a malformed request or bad parameters condition.
 * Maps to HTTP Status 400 (Bad Request).
 */
public class BadRequestException extends RuntimeException {
    
    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message explaining why the request was bad
     */
    public BadRequestException(String message) {
        super(message);
    }
}

