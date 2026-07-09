/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain;

import com.xebia.lms.domain.enums.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/**
 * Represents a platform user.
 *
 * This entity is owned by the IAM module and serves as the single source
 * of truth for authentication and authorization across the LMS ecosystem.
 *
 * Business Rules:
 * - Email must be unique.
 * - Primary keys must be UUIDs.
 * - Password must be stored as a BCrypt hash, never plain text.
 * - Permission version is versioned for Redis cache invalidation.
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "org_id")
    private UUID orgId;

    @Column(name = "sso_subject", length = 255)
    private String ssoSubject;

    @Builder.Default
    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone = "UTC";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus status = UserStatus.INVITED;

    @Builder.Default
    @Column(name = "permission_version", nullable = false)
    private Integer permissionVersion = 1;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}

