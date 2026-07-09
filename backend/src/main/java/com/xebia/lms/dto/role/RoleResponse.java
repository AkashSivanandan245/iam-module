/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing the details of a system role.
 *
 * @param id unique role UUID
 * @param name unique name of the role
 * @param description role description
 * @param permissions set of permission strings mapped to this role (e.g. USER:CREATE)
 * @param isSystem whether the role is a system role
 * @param createdAt timestamp when the role was registered
 */
public record RoleResponse(
    UUID id,
    String name,
    String description,
    Set<String> permissions,
    boolean isSystem,
    LocalDateTime createdAt
) {}

