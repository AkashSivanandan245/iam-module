/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain.enums;

/**
 * Represents the execution state of a Transactional Outbox Event.
 */
public enum OutboxStatus {
    /**
     * Event has been registered in the transaction but not yet published.
     */
    PENDING,

    /**
     * Event has been successfully parsed and published onto the event bus.
     */
    PROCESSED,

    /**
     * Event publishing failed permanently or exceeded retry count boundaries.
     */
    FAILED
}

