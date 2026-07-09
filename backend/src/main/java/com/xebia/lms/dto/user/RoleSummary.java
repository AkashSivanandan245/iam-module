/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import java.util.UUID;

/**
 * Data Transfer Object representing a summary of a system role.
 *
 * @param roleId unique role identity
 * @param name name of the role (e.g. ADMIN)
 * @param description brief description of the role responsibilities
 */
public record RoleSummary(
    UUID roleId,
    String name,
    String description
) {}

