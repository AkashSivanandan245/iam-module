/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.DomainEntity;
import com.xebia.lms.dto.masterdata.CreateDomainRequest;
import com.xebia.lms.dto.masterdata.DomainResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link DomainEntity} entities and domain DTOs.
 */
@Component
public class DomainMapper {

    /**
     * Converts a {@link DomainEntity} entity to a {@link DomainResponse} DTO.
     *
     * @param domain the DomainEntity
     * @return the populated DomainResponse DTO
     */
    public DomainResponse convertToResponse(DomainEntity domain) {
        if (domain == null) {
            return null;
        }

        return new DomainResponse(
            domain.getId(),
            domain.getName(),
            domain.getCreatedAt()
        );
    }

    /**
     * Maps a {@link CreateDomainRequest} DTO into a new {@link DomainEntity}.
     *
     * @param request the creation request details
     * @return the DomainEntity instance
     */
    public DomainEntity convertToEntity(CreateDomainRequest request) {
        if (request == null) {
            return null;
        }

        return DomainEntity.builder()
            .name(request.name())
            .build();
    }
}

