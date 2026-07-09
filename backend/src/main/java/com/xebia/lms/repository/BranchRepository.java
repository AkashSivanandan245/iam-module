/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.Branch;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Branch} persistence.
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    /**
     * Finds a branch by its name within a specific university.
     *
     * @param name name of the branch
     * @param universityId university UUID the branch belongs to
     * @return an Optional containing the matched Branch entity if found, otherwise empty
     */
    Optional<Branch> findByNameAndUniversityId(String name, UUID universityId);

    /**
     * Retrieves all branches belonging to a specific university.
     *
     * @param universityId university UUID
     * @return list of branches
     */
    List<Branch> findByUniversityId(UUID universityId);
}

