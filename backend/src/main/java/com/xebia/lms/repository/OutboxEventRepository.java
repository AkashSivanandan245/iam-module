/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.OutboxEvent;
import com.xebia.lms.domain.enums.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link OutboxEvent} persistence.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Finds outbox events of a specific status ordered by their creation timestamp.
     *
     * @param status the OutboxStatus status filter
     * @return matching list of OutboxEvents
     */
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}

