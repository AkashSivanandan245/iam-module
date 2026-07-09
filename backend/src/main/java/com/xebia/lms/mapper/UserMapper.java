/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import com.xebia.lms.dto.auth.MeResponse;
import com.xebia.lms.dto.user.CreateUserRequest;
import com.xebia.lms.dto.user.UserResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Mapper component that translates between {@link AppUser} domain entities
 * and various user-related Data Transfer Objects (DTOs).
 *
 * Helps maintain a strict boundary separation between external REST contracts
 * and internal database mapping schemas.
 */
@Component
public class UserMapper {

    /**
     * Converts an {@link AppUser} entity into a {@link UserResponse} DTO.
     *
     * @param user the AppUser entity
     * @return the populated UserResponse DTO
     */
    public UserResponse convertToResponse(AppUser user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
            user.getUserId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRoleId(),
            user.getOrgId(),
            user.getTimezone(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
    }

    /**
     * Converts an {@link AppUser} entity into a {@link MeResponse} DTO.
     *
     * @param user the AppUser entity
     * @return the populated MeResponse DTO
     */
    public MeResponse convertToMeResponse(AppUser user) {
        if (user == null) {
            return null;
        }

        return new MeResponse(
            user.getUserId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRoleId(),
            user.getOrgId(),
            user.getTimezone(),
            user.getStatus().name()
        );
    }

    /**
     * Maps a {@link CreateUserRequest} DTO into a new {@link AppUser} entity instance.
     * Note that the password hash must be set separately after encoding in the service layer.
     *
     * @param request the CreateUserRequest details
     * @return the populated AppUser entity
     */
    public AppUser convertToEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        String tz = StringUtils.hasText(request.timezone()) ? request.timezone() : "UTC";

        return AppUser.builder()
            .email(request.email())
            .displayName(request.displayName())
            .roleId(request.roleId())
            .orgId(request.organisationId())
            .timezone(tz)
            .status(UserStatus.INVITED) // Default onboarding state
            .permissionVersion(1)
            .build();
    }
}

