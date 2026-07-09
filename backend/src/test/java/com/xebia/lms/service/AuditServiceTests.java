/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.AuditLog;
import com.xebia.lms.domain.Role;
import com.xebia.lms.dto.audit.AuditLogResponse;
import com.xebia.lms.repository.AppUserRepository;
import com.xebia.lms.repository.AuditLogRepository;
import com.xebia.lms.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class AuditServiceTests {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    private AppUser testUser;
    private AppUser user2;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();

        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        AppUser user = new AppUser();
        user.setEmail("test-audit-" + UUID.randomUUID() + "@xebia.com");
        user.setPasswordHash("hash");
        user.setDisplayName("Test User");
        user.setRoleId(role.getRoleId());
        testUser = appUserRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        auditLogRepository.deleteAll();
        if (testUser != null) {
            appUserRepository.delete(testUser);
        }
        if (user2 != null) {
            appUserRepository.delete(user2);
        }
    }

    @Test
    void testLog_Success() {
        // Act
        auditService.log(testUser.getUserId(), "USER_CREATED", "USER", "user123", "127.0.0.1", "{\"role\":\"ADMIN\"}");

        // Assert
        assertThat(auditLogRepository.count()).isEqualTo(1);
        AuditLog auditLog = auditLogRepository.findAll().get(0);
        assertThat(auditLog.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(auditLog.getAction()).isEqualTo("USER_CREATED");
        assertThat(auditLog.getEntityType()).isEqualTo("USER");
        assertThat(auditLog.getEntityId()).isEqualTo("user123");
        assertThat(auditLog.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(auditLog.getDetails()).isEqualTo("{\"role\":\"ADMIN\"}");
    }

    @Test
    void testGetAuditLogs_Success() {
        user2 = new AppUser();
        user2.setEmail("test-audit2-" + UUID.randomUUID() + "@xebia.com");
        user2.setPasswordHash("hash");
        user2.setDisplayName("Test User 2");
        user2.setRoleId(testUser.getRoleId());
        user2 = appUserRepository.save(user2);

        auditService.log(testUser.getUserId(), "LOGIN", "SYSTEM", "system", "192.168.1.1", null);
        auditService.log(user2.getUserId(), "LOGOUT", "SYSTEM", "system", "192.168.1.2", null);

        // Act
        PageResponse<AuditLogResponse> response = auditService.getAuditLogs(0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);
    }
}
