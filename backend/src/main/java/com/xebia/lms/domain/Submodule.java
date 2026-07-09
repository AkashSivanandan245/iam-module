/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

/**
 * Represents a capability within a module.
 */
@Entity
@Table(name = "submodule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submodule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "submodule_id", updatable = false, nullable = false)
    private UUID submoduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "key", nullable = false, unique = true, length = 64)
    private String key;

    @Column(name = "title", length = 120)
    private String title;
}

