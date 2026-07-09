/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.University;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link University} persistence.
 */
@Repository
public interface UniversityRepository extends JpaRepository<University, UUID> {

    /**
     * Finds a university by its unique name within a specific organisation.
     *
     * @param name unique name of the university
     * @param organisationId organisation UUID the university belongs to
     * @return an Optional containing the matched University entity if found, otherwise empty
     */
    Optional<University> findByNameAndOrganisationId(String name, UUID organisationId);

    /**
     * Retrieves all universities belonging to a specific organisation.
     *
     * @param organisationId organisation UUID
     * @return list of universities
     */
    List<University> findByOrganisationId(UUID organisationId);
}

