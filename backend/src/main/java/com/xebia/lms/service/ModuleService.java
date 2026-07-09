/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.catalog.AuthorityMatrixResponse;
import com.xebia.lms.dto.catalog.AuthorityResponse;
import com.xebia.lms.dto.catalog.CreateModuleRequest;
import com.xebia.lms.dto.catalog.ModuleResponse;
import java.util.List;

/**
 * Service interface for Module and System Authority Catalog management.
 *
 * Responsibilities:
 * - Onboarding new modules.
 * - Auto-generating authorities (MODULE:ACTION combination matrix) for new modules.
 * - Retrieving lists of system authorities and catalog matrixes.
 */
public interface ModuleService {

    /**
     * Creates/onboards a new application module.
     *
     * Business Rules:
     * - Name must be unique.
     * - Automatically generates cross-product authorities (e.g. MODULE_NAME:ACTION_NAME)
     *   for all registered actions in the database.
     *
     * @param request module creation details
     * @return the created module response details
     * @throws com.xebia.lms.common.exception.ConflictException if module name already exists
     */
    ModuleResponse createModule(CreateModuleRequest request);

    /**
     * Lists all registered system modules.
     *
     * @return list of modules DTOs
     */
    List<ModuleResponse> getAllModules();

    /**
     * Lists all system authorities (e.g. USER:CREATE, COURSE:PUBLISH).
     *
     * @return list of authorities DTOs
     */
    List<AuthorityResponse> getAllAuthorities();

    /**
     * Retrieves the complete system matrix of modules, actions, and authorities.
     *
     * @return the authority matrix container DTO
     */
    AuthorityMatrixResponse getAuthorityMatrix();
}

