/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.Module;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Module} persistence.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    /**
     * Finds a module by its unique name (e.g. IAM, COURSES).
     *
     * @param key the unique key of the module
     * @return an Optional containing the matched Module entity if found, otherwise empty
     */
    Optional<Module> findByKey(String key);
}

