/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.lms.common.exception.BadRequestException;
import com.xebia.lms.domain.OutboxEvent;
import com.xebia.lms.domain.enums.OutboxStatus;
import com.xebia.lms.repository.OutboxEventRepository;
import com.xebia.lms.service.OutboxService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing the transactional outbox pattern.
 *
 * Responsibilities:
 * - Persisting business events within the main database transaction.
 * - Processing pending events in the background and updating their statuses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Serializes and saves an outbox event.
     *
     * Business Rules:
     * - Executed synchronously within the caller's transaction context.
     * - Payload must be serializable to JSON.
     *
     * @param eventType the type of the event (e.g., USER_CREATED)
     * @param payload   the object payload to be serialized
     */
    @Override
    @Transactional
    public void exportEvent(String eventType, Object payload) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new BadRequestException("Event type cannot be empty");
        }
        if (payload == null) {
            throw new BadRequestException("Payload cannot be null");
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            OutboxEvent event = OutboxEvent.builder()
                    .eventType(eventType)
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(event);
            log.info("Outbox event exported successfully: type={}", eventType);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for outbox event: type={}", eventType, e);
            throw new IllegalArgumentException("Failed to serialize outbox event payload", e);
        }
    }

    /**
     * Scheduled job to process pending outbox events.
     *
     * Business Rules:
     * - Fetches events with PENDING status.
     * - Attempts to publish each event.
     * - Updates status to PROCESSED on success, or increments retry/sets to FAILED on persistent errors.
     * - Configured via fixedRate (default 5 seconds).
     */
    @Scheduled(fixedRateString = "${app.outbox.schedule.fixed-rate:5000}")
    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return; // Nothing to process
        }

        log.debug("Found {} pending outbox events for processing", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // MOCK PUBLISH - In a real scenario, this would publish to a Kafka topic, RabbitMQ, SNS/SQS, etc.
                log.info("Publishing outbox event: id={}, type={}, payload={}", event.getId(), event.getEventType(), event.getPayload());
                
                // Mark as processed
                event.setStatus(OutboxStatus.PROCESSED);
                event.setProcessedAt(LocalDateTime.now());
                log.info("Successfully processed outbox event: id={}", event.getId());

            } catch (Exception e) {
                log.error("Failed to process outbox event: id={}", event.getId(), e);
                
                event.setRetryCount(event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
                
                // Allow up to 3 retries before marking as failed
                if (event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event exceeded max retries and marked as FAILED: id={}", event.getId());
                }
            }
        }
        
        // Let Hibernate's dirty checking or explicit saveAll update the DB
        outboxEventRepository.saveAll(pendingEvents);
    }
}

