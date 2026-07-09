/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.masterdata.CreateUniversityRequest;
import com.xebia.lms.dto.masterdata.UniversityResponse;
import com.xebia.lms.dto.masterdata.UpdateUniversityRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for University Management.
 */
public interface UniversityService {

    /**
     * Creates a new university.
     *
     * @param request creation request details
     * @return the created university details
     */
    UniversityResponse createUniversity(CreateUniversityRequest request);

    /**
     * Retrieves university details by ID.
     *
     * @param id university UUID
     * @return matching university details
     */
    UniversityResponse getUniversityById(UUID id);

    /**
     * Updates an existing university details.
     *
     * @param id university UUID
     * @param request update details
     * @return the updated university details
     */
    UniversityResponse updateUniversity(UUID id, UpdateUniversityRequest request);

    /**
     * Deletes a university.
     *
     * @param id university UUID
     */
    void deleteUniversity(UUID id);

    /**
     * Lists all universities belonging to a specific organisation.
     *
     * @param organisationId organisation UUID
     * @return list of universities
     */
    List<UniversityResponse> getUniversitiesByOrganisationId(UUID organisationId);

    /**
     * Lists all system universities.
     *
     * @return list of all universities DTOs
     */
    List<UniversityResponse> getAllUniversities();
}

