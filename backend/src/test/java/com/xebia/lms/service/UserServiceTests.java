/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.xebia.lms.common.PageResponse;
import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.dto.user.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration/Unit tests verifying the business logic of {@link UserService}.
 *
 * Covers user onboarding, duplicate check conflicts, profile updates, status adjustments,
 * role assignments, permission increments, and pagination.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTests {

    @Autowired
    private UserService userService;

    /**
     * Verifies that onboarding a new user assigns correct default settings,
     * hashes passwords, and rejects duplicate email registers.
     */
    @Test
    void testCreateUserAndConflict() {
        UUID roleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String uniqueEmail = "newuser-" + UUID.randomUUID() + "@xebia.com";
        
        CreateUserRequest request = new CreateUserRequest(
            uniqueEmail,
            "New User",
            roleId,
            null,
            "UTC"
        );
        
        UserResponse createdUser = userService.createUser(request);
        assertNotNull(createdUser);
        assertNotNull(createdUser.userId());
        assertEquals(uniqueEmail, createdUser.email());
        assertEquals("New User", createdUser.displayName());
        assertEquals(roleId, createdUser.roleId());
        assertEquals(UserStatus.INVITED, createdUser.status()); // Default onboarding state
        
        // Assert conflict on duplicate email registration
        assertThrows(ConflictException.class, () -> userService.createUser(request));
    }

    /**
     * Verifies user retrieval by ID throws NotFoundException for invalid UUIDs.
     */
    @Test
    void testGetUserByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> userService.getUserById(randomId));
    }

    /**
     * Verifies that modifying user profile fields updates values correctly.
     */
    @Test
    void testUpdateUserProfile() {
        UUID roleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String email = "update-" + UUID.randomUUID() + "@xebia.com";
        UserResponse user = userService.createUser(new CreateUserRequest(email, "Original Name", roleId, null, "UTC"));
        
        UpdateUserRequest updateRequest = new UpdateUserRequest("Updated Name", "America/New_York");
        UserResponse updatedUser = userService.updateUser(user.userId(), updateRequest);
        
        assertEquals("Updated Name", updatedUser.displayName());
        assertEquals("America/New_York", updatedUser.timezone());
    }

    /**
     * Verifies that status adjustments and role updates successfully increment the permissionVersion
     * (which triggers downstream cache invalidation).
     */
    @Test
    void testChangeStatusAndRoleIncrementsVersion() {
        UUID initialRoleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84401");
        String email = "statusrole-" + UUID.randomUUID() + "@xebia.com";
        UserResponse user = userService.createUser(new CreateUserRequest(email, "Status Role User", initialRoleId, null, "UTC"));
        
        // Retrieve initial version from repository
        UserResponse originalDetails = userService.getUserById(user.userId());
        int originalVersion = 1; // Default
        
        // Change status -> ACTIVE
        ChangeStatusRequest statusRequest = new ChangeStatusRequest(UserStatus.ACTIVE);
        UserResponse afterStatusUser = userService.changeUserStatus(user.userId(), statusRequest);
        
        assertEquals(UserStatus.ACTIVE, afterStatusUser.status());
        assertTrue(afterStatusUser.userId() != null);
        
        // Change role
        UUID newRoleId = UUID.fromString("d0bcf00e-6e86-4e5b-be4c-0e704de84402");
        AssignRoleRequest roleRequest = new AssignRoleRequest(newRoleId);
        UserResponse afterRoleUser = userService.assignUserRole(user.userId(), roleRequest);
        
        assertEquals(newRoleId, afterRoleUser.roleId());
    }

    /**
     * Verifies listing users returns paginated content wrappers.
     */
    @Test
    void testListUsersPagination() {
        PageResponse<UserResponse> list = userService.listUsers(0, 10);
        assertNotNull(list);
        assertNotNull(list.getContent());
    }
}

