/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.dto.audit.AuditLogResponse;
import java.util.UUID;

/**
 * Service interface managing platform-wide immutable Audit Logging.
 */
public interface AuditService {

    /**
     * Persists a new audit trail log asynchronously or inside existing transactional boundaries.
     *
     * @param userId user initiating the action
     * @param action category of action executed
     * @param entityType type of target entity
     * @param entityId target entity ID
     * @param ipAddress IP address from client request headers
     * @param details dynamic additional details serialized to JSON/text
     */
    void log(UUID userId, String action, String entityType, String entityId, String ipAddress, String details);

    /**
     * Retrieves paginated list of security audit logs.
     *
     * @param page page index (0-indexed)
     * @param size page size limits
     * @return paginated container response DTO
     */
    PageResponse<AuditLogResponse> getAuditLogs(int page, int size);
}

