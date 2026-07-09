/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object representing a request to onboard a new user.
 *
 * @param email user unique email address
 * @param displayName user profile name
 * @param roleId user initial role identity
 * @param organisationId organisation identity the user belongs to (optional)
 * @param timezone user preferred timezone, defaults to "UTC" if empty
 */
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Display name is required")
    String displayName,

    @NotNull(message = "Role ID is required")
    UUID roleId,

    UUID organisationId,

    String timezone
) {}

