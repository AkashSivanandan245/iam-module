/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.RolePermission;
import com.xebia.lms.domain.Role;
import com.xebia.lms.dto.role.CreateRoleRequest;
import com.xebia.lms.dto.role.RoleResponse;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link Role} entities and role-related DTOs.
 */
@Component
public class RoleMapper {

    /**
     * Converts a {@link Role} entity to a {@link RoleResponse} DTO.
     *
     * @param role the Role entity
     * @return the populated RoleResponse DTO
     */
    public RoleResponse convertToResponse(Role role) {
        if (role == null) {
            return null;
        }

        Set<String> permissionsSet = Collections.emptySet();
        if (role.getRolePermissions() != null) {
            permissionsSet = role.getRolePermissions().stream()
                .map(rp -> rp.getPermission().getCode())
                .collect(Collectors.toSet());
        }

        return new RoleResponse(
            role.getRoleId(),
            role.getName(),
            role.getDescription(),
            permissionsSet,
            role.isSystem(),
            role.getCreatedAt()
        );
    }

    /**
     * Maps a {@link CreateRoleRequest} DTO into a new {@link Role} entity.
     *
     * @param request the creation request details
     * @return the Role entity
     */
    public Role convertToEntity(CreateRoleRequest request) {
        if (request == null) {
            return null;
        }

        return Role.builder()
            .name(request.name())
            .description(request.description())
            .build();
    }
}

