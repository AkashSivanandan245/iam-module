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
 * Represents an academic branch or department.
 *
 * Belongs under a parent {@link University}. Mapped with unique constraint on branch name per university.
 */
@Entity
@Table(
    name = "branch",
    uniqueConstraints = @UniqueConstraint(name = "uq_branch_name_university", columnNames = {"name", "university_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "university_id", nullable = false)
    private UUID universityId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

