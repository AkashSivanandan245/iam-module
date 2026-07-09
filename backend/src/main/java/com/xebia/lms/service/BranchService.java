/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import com.xebia.lms.dto.masterdata.BranchResponse;
import com.xebia.lms.dto.masterdata.CreateBranchRequest;
import com.xebia.lms.dto.masterdata.UpdateBranchRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Branch Management.
 */
public interface BranchService {

    /**
     * Creates a new branch.
     *
     * @param request creation request details
     * @return the created branch details
     */
    BranchResponse createBranch(CreateBranchRequest request);

    /**
     * Retrieves branch details by ID.
     *
     * @param id branch UUID
     * @return matching branch details
     */
    BranchResponse getBranchById(UUID id);

    /**
     * Updates an existing branch details.
     *
     * @param id branch UUID
     * @param request update details
     * @return the updated branch details
     */
    BranchResponse updateBranch(UUID id, UpdateBranchRequest request);

    /**
     * Deletes a branch.
     *
     * @param id branch UUID
     */
    void deleteBranch(UUID id);

    /**
     * Lists all branches belonging to a specific university.
     *
     * @param universityId university UUID
     * @return list of branches
     */
    List<BranchResponse> getBranchesByUniversityId(UUID universityId);

    /**
     * Lists all system branches.
     *
     * @return list of all branches DTOs
     */
    List<BranchResponse> getAllBranches();
}

