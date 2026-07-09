/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.catalog;

import java.util.UUID;

/**
 * Data Transfer Object representing the details of an application module.
 *
 * @param id unique module UUID
 * @param name unique name of the module
 * @param description module description
 */
public record ModuleResponse(
    UUID moduleId,
    String key,
    String title,
    String icon,
    String route,
    boolean isEnabled
) {}

