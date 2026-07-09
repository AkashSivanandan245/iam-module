/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.audit;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a mapped security audit trail event log.
 */
public record AuditLogResponse(
    Long id,
    LocalDateTime timestamp,
    UUID userId,
    String action,
    String entityType,
    String entityId,
    String details,
    String ipAddress
) {}

