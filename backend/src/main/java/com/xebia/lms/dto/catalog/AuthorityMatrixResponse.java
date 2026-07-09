/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.dto.catalog;

import java.util.List;

/**
 * Data Transfer Object representing the matrix of all modules, actions, and resolved authorities.
 *
 * Useful for rendering user permission customization grids and role mapping lists.
 *
 * @param modules list of all registered modules
 * @param actions list of all registered system operations
 * @param authorities list of all generated module-action authority combinations
 */
public record AuthorityMatrixResponse(
    List<ModuleResponse> modules,
    List<ActionResponse> actions,
    List<AuthorityResponse> authorities
) {}

