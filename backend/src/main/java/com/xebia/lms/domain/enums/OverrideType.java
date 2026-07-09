/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain.enums;

/**
 * Represents the type of user authority override.
 *
 * ALLOW: Explicitly grants a permission that is not part of the user's role.
 * DENY: Explicitly revokes a permission that is part of the user's role.
 */
public enum OverrideType {
    ALLOW,
    DENY
}

