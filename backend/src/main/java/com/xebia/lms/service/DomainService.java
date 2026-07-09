/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.masterdata.CreateDomainRequest;
import com.xebia.lms.dto.masterdata.DomainResponse;
import com.xebia.lms.dto.masterdata.UpdateDomainRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Domain Taxonomy Management.
 */
public interface DomainService {

    /**
     * Creates a new learning domain.
     *
     * @param request creation request details
     * @return the created domain details
     */
    DomainResponse createDomain(CreateDomainRequest request);

    /**
     * Retrieves domain details by ID.
     *
     * @param id domain UUID
     * @return matching domain details
     */
    DomainResponse getDomainById(UUID id);

    /**
     * Updates an existing domain details.
     *
     * @param id domain UUID
     * @param request update details
     * @return the updated domain details
     */
    DomainResponse updateDomain(UUID id, UpdateDomainRequest request);

    /**
     * Deletes a domain.
     *
     * @param id domain UUID
     */
    void deleteDomain(UUID id);

    /**
     * Lists all learning domains.
     *
     * @return list of all domains
     */
    List<DomainResponse> getAllDomains();
}

