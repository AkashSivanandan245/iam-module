/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.Organisation;
import com.xebia.lms.dto.masterdata.CreateOrganisationRequest;
import com.xebia.lms.dto.masterdata.OrganisationResponse;
import com.xebia.lms.dto.masterdata.UpdateOrganisationRequest;
import com.xebia.lms.mapper.OrganisationMapper;
import com.xebia.lms.repository.OrganisationRepository;
import com.xebia.lms.service.OrganisationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing Organisation master-data workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final OrganisationMapper organisationMapper;

    @Override
    @Transactional
    public OrganisationResponse createOrganisation(CreateOrganisationRequest request) {
        log.info("Creating organisation: {}", request.name());

        Optional<Organisation> existing = organisationRepository.findByName(request.name());
        if (existing.isPresent()) {
            log.warn("Organisation name conflict: {}", request.name());
            throw new ConflictException("Organisation with name " + request.name() + " already exists");
        }

        Organisation org = organisationMapper.convertToEntity(request);
        Organisation saved = organisationRepository.save(org);
        log.info("Organisation created successfully. ID: {}", saved.getOrgId());

        return organisationMapper.convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationResponse getOrganisationById(UUID id) {
        log.info("Retrieving organisation ID: {}", id);
        
        Organisation org = organisationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Organisation with ID " + id + " not found"));

        return organisationMapper.convertToResponse(org);
    }

    @Override
    @Transactional
    public OrganisationResponse updateOrganisation(UUID id, UpdateOrganisationRequest request) {
        log.info("Updating organisation ID: {}", id);

        Organisation org = organisationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Organisation with ID " + id + " not found"));

        Optional<Organisation> existingWithName = organisationRepository.findByName(request.name());
        if (existingWithName.isPresent() && !existingWithName.get().getOrgId().equals(id)) {
            log.warn("Organisation name conflict on update: {}", request.name());
            throw new ConflictException("Organisation with name " + request.name() + " already exists");
        }

        org.setName(request.name());
        Organisation updated = organisationRepository.save(org);
        log.info("Organisation updated successfully. ID: {}", id);

        return organisationMapper.convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteOrganisation(UUID id) {
        log.info("Deleting organisation ID: {}", id);

        Organisation org = organisationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Organisation with ID " + id + " not found"));

        organisationRepository.delete(org);
        log.info("Organisation deleted successfully. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationResponse> getAllOrganisations() {
        log.info("Retrieving all organisations");
        return organisationRepository.findAll().stream()
            .map(organisationMapper::convertToResponse)
            .toList();
    }
}

