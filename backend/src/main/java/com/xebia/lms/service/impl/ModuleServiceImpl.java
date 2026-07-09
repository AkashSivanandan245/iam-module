/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service.impl;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.domain.Module;
import com.xebia.lms.domain.Permission;
import com.xebia.lms.dto.catalog.*;
import com.xebia.lms.mapper.ModuleMapper;
import com.xebia.lms.repository.ModuleRepository;
import com.xebia.lms.repository.PermissionRepository;
import com.xebia.lms.service.ModuleService;
import com.xebia.lms.domain.Submodule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation managing Module and Authority Catalog business logic.
 *
 * Automatically generates matrix authorities upon module creation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final com.xebia.lms.repository.SubmoduleRepository submoduleRepository;
    private final PermissionRepository permissionRepository;
    private final ModuleMapper moduleMapper;

    private static final List<String> STANDARD_ACTIONS = List.of("VIEW", "CREATE", "UPDATE", "DELETE", "DECIDE");

    @Override
    @Transactional
    public ModuleResponse createModule(CreateModuleRequest request) {
        log.info("Attempting to onboarding new module: {}", request.key());

        Optional<Module> existingModule = moduleRepository.findByKey(request.key());
        if (existingModule.isPresent()) {
            log.warn("Module creation aborted. Module key already registered: {}", request.key());
            throw new ConflictException("Module with key " + request.key() + " already exists");
        }

        Module module = moduleMapper.convertToEntity(request);
        Module savedModule = moduleRepository.save(module);
        
        Submodule defaultSubmodule = Submodule.builder()
            .module(savedModule)
            .key(savedModule.getKey() + "_DEF")
            .title(savedModule.getTitle() + " Default Submodule")
            .build();
        submoduleRepository.save(defaultSubmodule);
        
        // Auto-generate SUBMODULE:ACTION permissions
        List<Permission> permissionsToSave = new ArrayList<>();
        
        log.info("Generating dynamic permissions for module: {}", savedModule.getKey());
        for (String action : STANDARD_ACTIONS) {
            String code = (savedModule.getKey() + ":" + action).toUpperCase();
            
            Permission permission = Permission.builder()
                .submodule(defaultSubmodule)
                .code(code)
                .description("Can " + action + " in " + savedModule.getKey())
                .build();
                
            permissionsToSave.add(permission);
        }
        
        permissionRepository.saveAll(permissionsToSave);
        log.info("Module onboarding and permission generation completed. Module ID: {}", savedModule.getModuleId());

        return moduleMapper.convertToResponse(savedModule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> getAllModules() {
        log.info("Retrieving all modules");
        return moduleRepository.findAll().stream()
            .map(moduleMapper::convertToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityResponse> getAllAuthorities() {
        log.info("Retrieving all permissions");
        return permissionRepository.findAll().stream()
            .map(p -> new AuthorityResponse(p.getPermissionId(), p.getCode()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorityMatrixResponse getAuthorityMatrix() {
        log.info("Building catalog permission matrix");
        
        List<ModuleResponse> modules = moduleRepository.findAll().stream()
            .map(moduleMapper::convertToResponse)
            .toList();
            
        List<ActionResponse> actions = STANDARD_ACTIONS.stream()
            .map(a -> new ActionResponse(UUID.randomUUID(), a, a)) // Generate dummy UUIDs for actions if UI needs them
            .toList();
            
        List<AuthorityResponse> authorities = permissionRepository.findAll().stream()
            .map(p -> new AuthorityResponse(p.getPermissionId(), p.getCode()))
            .toList();
            
        return new AuthorityMatrixResponse(modules, actions, authorities);
    }
}

