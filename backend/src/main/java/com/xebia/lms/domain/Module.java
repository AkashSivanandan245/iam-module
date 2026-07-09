/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents an application module in the LMS ecosystem.
 *
 * This entity is owned by the IAM module and defines logical boundaries
 * for dynamic RBAC authorities (e.g. IAM, COURSES, ASSESSMENTS).
 */
@Entity
@Table(name = "module")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "module_id", updatable = false, nullable = false)
    private UUID moduleId;

    @Column(name = "key", nullable = false, unique = true, length = 64)
    private String key;

    @Column(name = "title", length = 120)
    private String title;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "route", length = 120)
    private String route;

    @Builder.Default
    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

