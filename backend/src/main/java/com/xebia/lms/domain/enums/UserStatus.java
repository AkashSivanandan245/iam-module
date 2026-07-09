/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.domain.enums;

/**
 * Represents the status of a platform user.
 *
 * Statuses:
 * - ACTIVE: The user is fully active and can log in and perform operations.
 * - SUSPENDED: The user is suspended by an administrator and is blocked from accessing the system.
 * - INVITED: The user is created via invitation but has not yet set up their password or finalized onboarding.
 */
public enum UserStatus {
    /**
     * User is active and can fully access system resources based on their permissions.
     */
    ACTIVE,

    /**
     * User accounts are suspended. Login is blocked.
     */
    SUSPENDED,

    /**
     * User has been invited but has not yet completed the initial password setup and verification.
     */
    INVITED
}

