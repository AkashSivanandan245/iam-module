/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common.exception;

/**
 * Custom exception representing a resource not found condition.
 * Maps to HTTP Status 404 (Not Found).
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which resource is missing
     */
    public NotFoundException(String message) {
        super(message);
    }
}

