/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.domain.AuditLog;
import com.xebia.lms.dto.audit.AuditLogResponse;
import com.xebia.lms.repository.AuditLogRepository;
import com.xebia.lms.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service implementation managing Audit Trail persistence workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, String action, String entityType, String entityId, String ipAddress, String details) {
        log.debug("Recording audit log. Actor: {}, Action: {}, Entity: {}:{}", userId, action, entityType, entityId);

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .ipAddress(ipAddress)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getAuditLogs(int page, int size) {
        log.info("Retrieving paginated audit logs. Page: {}, Size: {}", page, size);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> auditPage = auditLogRepository.findAll(pageRequest);

        return new PageResponse<>(
                auditPage.getContent().stream()
                        .map(entry -> new AuditLogResponse(
                                entry.getId(),
                                entry.getTimestamp(),
                                entry.getUserId(),
                                entry.getAction(),
                                entry.getEntityType(),
                                entry.getEntityId(),
                                entry.getDetails(),
                                entry.getIpAddress()
                        ))
                        .toList(),
                auditPage.getNumber(),
                auditPage.getSize(),
                auditPage.getTotalElements(),
                auditPage.getTotalPages(),
                auditPage.isLast()
        );
    }

    /**
     * Resolves the client IP from the current HTTP request context.
     * Prefers X-Forwarded-For when behind a reverse proxy.
     */
    public static String resolveClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}