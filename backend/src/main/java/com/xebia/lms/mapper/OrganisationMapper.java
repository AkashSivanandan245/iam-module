/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.Organisation;
import com.xebia.lms.dto.masterdata.CreateOrganisationRequest;
import com.xebia.lms.dto.masterdata.OrganisationResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link Organisation} entities and organisation DTOs.
 */
@Component
public class OrganisationMapper {

    /**
     * Converts an {@link Organisation} entity to an {@link OrganisationResponse} DTO.
     *
     * @param organisation the Organisation entity
     * @return the populated OrganisationResponse DTO
     */
    public OrganisationResponse convertToResponse(Organisation organisation) {
        if (organisation == null) {
            return null;
        }

        return new OrganisationResponse(
            organisation.getOrgId(),
            organisation.getName(),
            organisation.getCreatedAt()
        );
    }

    /**
     * Maps a {@link CreateOrganisationRequest} DTO into a new {@link Organisation} entity.
     *
     * @param request the creation request details
     * @return the Organisation entity
     */
    public Organisation convertToEntity(CreateOrganisationRequest request) {
        if (request == null) {
            return null;
        }

        return Organisation.builder()
            .name(request.name())
            .build();
    }
}

