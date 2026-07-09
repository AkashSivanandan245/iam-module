/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.University;
import com.xebia.lms.dto.masterdata.CreateUniversityRequest;
import com.xebia.lms.dto.masterdata.UniversityResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link University} entities and university DTOs.
 */
@Component
public class UniversityMapper {

    /**
     * Converts a {@link University} entity to a {@link UniversityResponse} DTO.
     *
     * @param university the University entity
     * @return the populated UniversityResponse DTO
     */
    public UniversityResponse convertToResponse(University university) {
        if (university == null) {
            return null;
        }

        return new UniversityResponse(
            university.getId(),
            university.getName(),
            university.getOrganisationId(),
            university.getCreatedAt()
        );
    }

    /**
     * Maps a {@link CreateUniversityRequest} DTO into a new {@link University} entity.
     *
     * @param request the creation request details
     * @return the University entity
     */
    public University convertToEntity(CreateUniversityRequest request) {
        if (request == null) {
            return null;
        }

        return University.builder()
            .name(request.name())
            .organisationId(request.organisationId())
            .build();
    }
}

