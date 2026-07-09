/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.dto.audit.AuditLogResponse;
import com.xebia.lms.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing REST endpoints for querying Audit Logs.
 * 
 * Responsibilities:
 * - Handling requests to retrieve security audit trails.
 * - Enforcing access controls (`AUDIT:READ`).
 */
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit Logging APIs")
public class AuditController {

    private final AuditService auditService;

    /**
     * Retrieves paginated security audit logs.
     * 
     * @param page page index (defaults to 0)
     * @param size page size (defaults to 20)
     * @return paginated response containing audit logs
     */
    @Operation(
            summary = "Get audit logs",
            description = "Retrieves a paginated list of system audit logs. Requires AUDIT:READ permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADM:AUDIT:VIEW')")
    public ResponseEntity<PageResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditLogs(page, size));
    }
}

