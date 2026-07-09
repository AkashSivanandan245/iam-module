/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

public interface OutboxService {

    /**
     * Serializes the payload and saves it as a PENDING outbox event.
     * This should participate in the active transaction.
     *
     * @param eventType the type of the event (e.g., USER_CREATED)
     * @param payload   the object payload to be serialized
     */
    void exportEvent(String eventType, Object payload);
}

