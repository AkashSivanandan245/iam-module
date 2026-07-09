/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.Branch;
import com.xebia.lms.dto.masterdata.BranchResponse;
import com.xebia.lms.dto.masterdata.CreateBranchRequest;
import com.xebia.lms.dto.masterdata.UpdateBranchRequest;
import com.xebia.lms.mapper.BranchMapper;
import com.xebia.lms.repository.BranchRepository;
import com.xebia.lms.repository.UniversityRepository;
import com.xebia.lms.service.BranchService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing Branch master-data workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final UniversityRepository universityRepository;
    private final BranchMapper branchMapper;

    @Override
    @Transactional
    public BranchResponse createBranch(CreateBranchRequest request) {
        log.info("Creating branch: {} for university ID: {}", request.name(), request.universityId());

        // Validate parent university exists
        if (!universityRepository.existsById(request.universityId())) {
            throw new NotFoundException("University with ID " + request.universityId() + " not found");
        }

        Optional<Branch> existing = branchRepository.findByNameAndUniversityId(request.name(), request.universityId());
        if (existing.isPresent()) {
            log.warn("Branch name conflict for name: {} under university: {}", request.name(), request.universityId());
            throw new ConflictException("Branch with name " + request.name() + " already exists in this university");
        }

        Branch branch = branchMapper.convertToEntity(request);
        Branch saved = branchRepository.save(branch);
        log.info("Branch created successfully. ID: {}", saved.getId());

        return branchMapper.convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(UUID id) {
        log.info("Retrieving branch ID: {}", id);
        
        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Branch with ID " + id + " not found"));

        return branchMapper.convertToResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(UUID id, UpdateBranchRequest request) {
        log.info("Updating branch ID: {}", id);

        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Branch with ID " + id + " not found"));

        // Validate parent university exists
        if (!universityRepository.existsById(request.universityId())) {
            throw new NotFoundException("University with ID " + request.universityId() + " not found");
        }

        Optional<Branch> existingWithName = branchRepository.findByNameAndUniversityId(request.name(), request.universityId());
        if (existingWithName.isPresent() && !existingWithName.get().getId().equals(id)) {
            log.warn("Branch name conflict on update: {} under university: {}", request.name(), request.universityId());
            throw new ConflictException("Branch with name " + request.name() + " already exists in this university");
        }

        branch.setName(request.name());
        branch.setUniversityId(request.universityId());
        
        Branch updated = branchRepository.save(branch);
        log.info("Branch updated successfully. ID: {}", id);

        return branchMapper.convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBranch(UUID id) {
        log.info("Deleting branch ID: {}", id);

        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Branch with ID " + id + " not found"));

        branchRepository.delete(branch);
        log.info("Branch deleted successfully. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getBranchesByUniversityId(UUID universityId) {
        log.info("Retrieving branches for university ID: {}", universityId);
        return branchRepository.findByUniversityId(universityId).stream()
            .map(branchMapper::convertToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        log.info("Retrieving all branches");
        return branchRepository.findAll().stream()
            .map(branchMapper::convertToResponse)
            .toList();
    }
}

