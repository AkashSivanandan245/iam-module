/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.domain.DomainEntity;
import com.xebia.lms.dto.masterdata.CreateDomainRequest;
import com.xebia.lms.dto.masterdata.DomainResponse;
import com.xebia.lms.dto.masterdata.UpdateDomainRequest;
import com.xebia.lms.mapper.DomainMapper;
import com.xebia.lms.repository.DomainRepository;
import com.xebia.lms.service.DomainService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing Domain taxonomy workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final DomainMapper domainMapper;

    @Override
    @Transactional
    public DomainResponse createDomain(CreateDomainRequest request) {
        log.info("Creating learning domain: {}", request.name());

        Optional<DomainEntity> existing = domainRepository.findByName(request.name());
        if (existing.isPresent()) {
            log.warn("Domain name conflict: {}", request.name());
            throw new ConflictException("Domain with name " + request.name() + " already exists");
        }

        DomainEntity domain = domainMapper.convertToEntity(request);
        DomainEntity saved = domainRepository.save(domain);
        log.info("Domain created successfully. ID: {}", saved.getId());

        return domainMapper.convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DomainResponse getDomainById(UUID id) {
        log.info("Retrieving domain ID: {}", id);
        
        DomainEntity domain = domainRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Domain with ID " + id + " not found"));

        return domainMapper.convertToResponse(domain);
    }

    @Override
    @Transactional
    public DomainResponse updateDomain(UUID id, UpdateDomainRequest request) {
        log.info("Updating domain ID: {}", id);

        DomainEntity domain = domainRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Domain with ID " + id + " not found"));

        Optional<DomainEntity> existingWithName = domainRepository.findByName(request.name());
        if (existingWithName.isPresent() && !existingWithName.get().getId().equals(id)) {
            log.warn("Domain name conflict on update: {}", request.name());
            throw new ConflictException("Domain with name " + request.name() + " already exists");
        }

        domain.setName(request.name());
        DomainEntity updated = domainRepository.save(domain);
        log.info("Domain updated successfully. ID: {}", id);

        return domainMapper.convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDomain(UUID id) {
        log.info("Deleting domain ID: {}", id);

        DomainEntity domain = domainRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Domain with ID " + id + " not found"));

        domainRepository.delete(domain);
        log.info("Domain deleted successfully. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainResponse> getAllDomains() {
        log.info("Retrieving all learning domains");
        return domainRepository.findAll().stream()
            .map(domainMapper::convertToResponse)
            .toList();
    }
}

