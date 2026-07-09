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
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Represents a corporate or institutional tenant (Organisation).
 *
 * This entity is owned by the IAM module and serves as a root of administrative containment.
 */
@Entity
@Table(name = "organisation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "org_id", updatable = false, nullable = false)
    private UUID orgId;

    @Column(name = "name", length = 120)
    private String name;

    @Column(name = "domain", unique = true, length = 120)
    private String domain;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "theme_json")
    private String themeJson;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

