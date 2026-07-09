/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object representing a request to update an existing user's profile details.
 *
 * @param displayName user profile name
 * @param timezone user preferred timezone
 */
public record UpdateUserRequest(
    @NotBlank(message = "Display name is required")
    String displayName,

    @NotBlank(message = "Timezone is required")
    String timezone
) {}

