/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.masterdata.CreateOrganisationRequest;
import com.xebia.lms.dto.masterdata.OrganisationResponse;
import com.xebia.lms.dto.masterdata.UpdateOrganisationRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Organisation Management.
 */
public interface OrganisationService {

    /**
     * Creates a new organisation.
     *
     * @param request creation request details
     * @return the created organisation details
     * @throws com.xebia.lms.common.exception.ConflictException if name is duplicate
     */
    OrganisationResponse createOrganisation(CreateOrganisationRequest request);

    /**
     * Retrieves organisation details by ID.
     *
     * @param id organisation UUID
     * @return matching organisation details
     * @throws com.xebia.lms.common.exception.NotFoundException if missing
     */
    OrganisationResponse getOrganisationById(UUID id);

    /**
     * Updates an existing organisation details.
     *
     * @param id organisation UUID
     * @param request update details
     * @return the updated organisation details
     */
    OrganisationResponse updateOrganisation(UUID id, UpdateOrganisationRequest request);

    /**
     * Deletes an organisation.
     *
     * @param id organisation UUID
     */
    void deleteOrganisation(UUID id);

    /**
     * Lists all system organisations.
     *
     * @return list of organisations
     */
    List<OrganisationResponse> getAllOrganisations();
}

