/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.DomainEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link DomainEntity} persistence.
 */
@Repository
public interface DomainRepository extends JpaRepository<DomainEntity, UUID> {

    /**
     * Finds a learning domain by its unique name.
     *
     * @param name unique name of the domain
     * @return an Optional containing the matched DomainEntity if found, otherwise empty
     */
    Optional<DomainEntity> findByName(String name);
}

