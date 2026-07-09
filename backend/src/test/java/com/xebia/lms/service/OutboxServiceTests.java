/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.xebia.lms.common.exception.BadRequestException;
import com.xebia.lms.domain.OutboxEvent;
import com.xebia.lms.domain.enums.OutboxStatus;
import com.xebia.lms.repository.OutboxEventRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OutboxServiceTests {

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @AfterEach
    void tearDown() {
        outboxEventRepository.deleteAll();
    }

    @Test
    void testExportEvent_Success() {
        // Arrange
        Map<String, String> payload = Map.of("userId", "123", "email", "test@test.com");

        // Act
        outboxService.exportEvent("USER_CREATED", payload);

        // Assert
        List<OutboxEvent> events = outboxEventRepository.findAll();
        assertThat(events).hasSize(1);
        OutboxEvent event = events.get(0);
        assertThat(event.getEventType()).isEqualTo("USER_CREATED");
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(event.getPayload()).contains("\"userId\":\"123\"");
        assertThat(event.getPayload()).contains("\"email\":\"test@test.com\"");
        assertThat(event.getRetryCount()).isEqualTo(0);
    }

    @Test
    void testExportEvent_EmptyEventType_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> outboxService.exportEvent("", "payload"));
    }

    @Test
    void testExportEvent_NullPayload_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> outboxService.exportEvent("TYPE", null));
    }
}

