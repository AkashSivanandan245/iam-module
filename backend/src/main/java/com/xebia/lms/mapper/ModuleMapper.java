/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.mapper;

import com.xebia.lms.domain.Module;
import com.xebia.lms.dto.catalog.CreateModuleRequest;
import com.xebia.lms.dto.catalog.ModuleResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper component that translates between {@link Module} entities and module-related DTOs.
 */
@Component
public class ModuleMapper {

    /**
     * Converts a {@link Module} entity to a {@link ModuleResponse} DTO.
     *
     * @param module the Module entity
     * @return the populated ModuleResponse DTO
     */
    public ModuleResponse convertToResponse(Module module) {
        if (module == null) {
            return null;
        }

        return new ModuleResponse(
            module.getModuleId(),
            module.getKey(),
            module.getTitle(),
            module.getIcon(),
            module.getRoute(),
            module.isEnabled()
        );
    }

    /**
     * Maps a {@link CreateModuleRequest} DTO into a new {@link Module} entity.
     *
     * @param request the creation request details
     * @return the Module entity
     */
    public Module convertToEntity(CreateModuleRequest request) {
        if (request == null) {
            return null;
        }

        return Module.builder()
            .key(request.key())
            .title(request.title())
            .icon(request.icon())
            .route(request.route())
            .isEnabled(true)
            .build();
    }
}

