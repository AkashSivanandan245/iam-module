/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.Branch;
import com.xebia.lms.dto.masterdata.CreateBranchRequest;
import com.xebia.lms.dto.masterdata.BranchResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link Branch} entities and branch DTOs.
 */
@Component
public class BranchMapper {

    /**
     * Converts a {@link Branch} entity to a {@link BranchResponse} DTO.
     *
     * @param branch the Branch entity
     * @return the populated BranchResponse DTO
     */
    public BranchResponse convertToResponse(Branch branch) {
        if (branch == null) {
            return null;
        }

        return new BranchResponse(
            branch.getId(),
            branch.getName(),
            branch.getUniversityId(),
            branch.getCreatedAt()
        );
    }

    /**
     * Maps a {@link CreateBranchRequest} DTO into a new {@link Branch} entity.
     *
     * @param request the creation request details
     * @return the Branch entity
     */
    public Branch convertToEntity(CreateBranchRequest request) {
        if (request == null) {
            return null;
        }

        return Branch.builder()
            .name(request.name())
            .universityId(request.universityId())
            .build();
    }
}

