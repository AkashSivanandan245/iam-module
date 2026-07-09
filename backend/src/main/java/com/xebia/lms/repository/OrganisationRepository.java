/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.Organisation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Organisation} persistence.
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {

    /**
     * Finds an organisation by its unique name.
     *
     * @param name unique name of the organisation
     * @return an Optional containing the matched Organisation entity if found, otherwise empty
     */
    Optional<Organisation> findByName(String name);
}

