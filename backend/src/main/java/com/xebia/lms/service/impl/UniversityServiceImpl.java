/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.University;
import com.xebia.lms.dto.masterdata.CreateUniversityRequest;
import com.xebia.lms.dto.masterdata.UniversityResponse;
import com.xebia.lms.dto.masterdata.UpdateUniversityRequest;
import com.xebia.lms.mapper.UniversityMapper;
import com.xebia.lms.repository.OrganisationRepository;
import com.xebia.lms.repository.UniversityRepository;
import com.xebia.lms.service.UniversityService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing University master-data workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository universityRepository;
    private final OrganisationRepository organisationRepository;
    private final UniversityMapper universityMapper;

    @Override
    @Transactional
    public UniversityResponse createUniversity(CreateUniversityRequest request) {
        log.info("Creating university: {} for org ID: {}", request.name(), request.organisationId());

        // Validate parent organisation exists
        if (!organisationRepository.existsById(request.organisationId())) {
            throw new NotFoundException("Organisation with ID " + request.organisationId() + " not found");
        }

        Optional<University> existing = universityRepository.findByNameAndOrganisationId(request.name(), request.organisationId());
        if (existing.isPresent()) {
            log.warn("University name conflict for name: {} under org: {}", request.name(), request.organisationId());
            throw new ConflictException("University with name " + request.name() + " already exists in this organisation");
        }

        University uni = universityMapper.convertToEntity(request);
        University saved = universityRepository.save(uni);
        log.info("University created successfully. ID: {}", saved.getId());

        return universityMapper.convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UniversityResponse getUniversityById(UUID id) {
        log.info("Retrieving university ID: {}", id);
        
        University uni = universityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("University with ID " + id + " not found"));

        return universityMapper.convertToResponse(uni);
    }

    @Override
    @Transactional
    public UniversityResponse updateUniversity(UUID id, UpdateUniversityRequest request) {
        log.info("Updating university ID: {}", id);

        University uni = universityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("University with ID " + id + " not found"));

        // Validate parent organisation exists
        if (!organisationRepository.existsById(request.organisationId())) {
            throw new NotFoundException("Organisation with ID " + request.organisationId() + " not found");
        }

        Optional<University> existingWithName = universityRepository.findByNameAndOrganisationId(request.name(), request.organisationId());
        if (existingWithName.isPresent() && !existingWithName.get().getId().equals(id)) {
            log.warn("University name conflict on update: {} under org: {}", request.name(), request.organisationId());
            throw new ConflictException("University with name " + request.name() + " already exists in this organisation");
        }

        uni.setName(request.name());
        uni.setOrganisationId(request.organisationId());
        
        University updated = universityRepository.save(uni);
        log.info("University updated successfully. ID: {}", id);

        return universityMapper.convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUniversity(UUID id) {
        log.info("Deleting university ID: {}", id);

        University uni = universityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("University with ID " + id + " not found"));

        universityRepository.delete(uni);
        log.info("University deleted successfully. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityResponse> getUniversitiesByOrganisationId(UUID organisationId) {
        log.info("Retrieving universities for org ID: {}", organisationId);
        return universityRepository.findByOrganisationId(organisationId).stream()
            .map(universityMapper::convertToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityResponse> getAllUniversities() {
        log.info("Retrieving all universities");
        return universityRepository.findAll().stream()
            .map(universityMapper::convertToResponse)
            .toList();
    }
}

