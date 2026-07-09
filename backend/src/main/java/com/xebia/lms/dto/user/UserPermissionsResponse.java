/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing the effective permissions assigned to a user.
 *
 * @param userId unique user identity
 * @param email user email address
 * @param roleName name of the user primary role
 * @param permissions list of string representation of effective authorities (e.g. USER:CREATE)
 */
public record UserPermissionsResponse(
    UUID userId,
    String email,
    String roleName,
    Set<String> permissions
) {}

