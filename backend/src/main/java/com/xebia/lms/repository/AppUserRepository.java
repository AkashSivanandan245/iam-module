/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.repository;

import com.xebia.lms.domain.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AppUser} persistence.
 *
 * Exposes standard CRUD operations and custom query methods for accessing
 * platform user identities from the database.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    /**
     * Finds an active or invited user in the platform by their unique email address.
     *
     * Business Rules:
     * - Retrived value is wrapped in an {@link Optional} container to handle
     *   null states cleanly, avoiding NullPointerException.
     *
     * @param email the unique email address to query
     * @return an Optional containing the matched user entity if found, otherwise empty
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Retrieves the role name associated with a user by their user ID.
     *
     * @param userId the target user's UUID
     * @return an Optional containing the role name, or empty if not found
     */
    @Query(value = "SELECT r.name FROM role r JOIN app_user u ON u.role_id = r.role_id WHERE u.user_id = :userId", nativeQuery = true)
    Optional<String> findRoleNameByUserId(@Param("userId") UUID userId);

    /**
     * Retrieves all authority names granted to a user via their primary role.
     *
     * @param userId the target user's UUID
     * @return list of authority names (e.g. USER:CREATE)
     */
    @Query(value = "SELECT p.code FROM permission p " +
                   "JOIN role_permission rp ON rp.permission_id = p.permission_id " +
                   "JOIN app_user u ON u.role_id = rp.role_id " +
                   "WHERE u.user_id = :userId", nativeQuery = true)
    List<String> findRoleAuthoritiesByUserId(@Param("userId") UUID userId);

    /**
     * Increments the permission version for all users assigned to a specific role.
     * Used for bulk cache invalidation when role authorities are modified.
     *
     * @param roleId the role UUID to target
     */
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AppUser u SET u.permissionVersion = u.permissionVersion + 1 WHERE u.roleId = :roleId")
    void incrementPermissionVersionByRoleId(@Param("roleId") UUID roleId);
}

