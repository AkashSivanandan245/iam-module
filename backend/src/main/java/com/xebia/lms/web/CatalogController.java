/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.dto.catalog.AuthorityMatrixResponse;
import com.xebia.lms.dto.catalog.AuthorityResponse;
import com.xebia.lms.dto.catalog.CreateModuleRequest;
import com.xebia.lms.dto.catalog.ModuleResponse;
import com.xebia.lms.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for managing system modules and authority catalogs.
 *
 * All operations require explicit client pre-authorization.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Catalog Management", description = "Endpoints for registering application modules, listing authorities, and inspecting security matrixes")
public class CatalogController {

    private final ModuleService moduleService;

    /**
     * Registers a new module and automatically generates all MODULE:ACTION authorities.
     *
     * @param request creation request details
     * @return ResponseEntity holding the created ModuleResponse DTO
     */
    @Operation(summary = "Create module", description = "Registers a new system module and auto-generates all cross-product MODULE:ACTION permission authorities.")
    @PreAuthorize("hasAuthority('ADM:RBAC:MANAGE')")
    @PostMapping("/admin/modules")
    public ResponseEntity<ModuleResponse> createModule(@Valid @RequestBody CreateModuleRequest request) {
        log.info("REST request to register new module: {}", request.key());
        ModuleResponse response = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all registered system modules.
     *
     * @return ResponseEntity holding list of ModuleResponse DTOs
     */
    @Operation(summary = "List modules", description = "Retrieves all registered application modules.")
    @PreAuthorize("hasAuthority('CATALOG:READ')")
    @GetMapping("/modules")
    public ResponseEntity<List<ModuleResponse>> getAllModules() {
        log.info("REST request to list all modules");
        List<ModuleResponse> response = moduleService.getAllModules();
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all generated system authorities.
     *
     * @return ResponseEntity holding list of AuthorityResponse DTOs
     */
    @Operation(summary = "List authorities", description = "Retrieves all generated system permission authorities.")
    @PreAuthorize("hasAuthority('CATALOG:READ')")
    @GetMapping("/authorities")
    public ResponseEntity<List<AuthorityResponse>> getAllAuthorities() {
        log.info("REST request to list all system authorities");
        List<AuthorityResponse> response = moduleService.getAllAuthorities();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the complete system matrix of modules, actions, and authorities.
     *
     * @return ResponseEntity holding AuthorityMatrixResponse DTO
     */
    @Operation(summary = "Get authority matrix", description = "Retrieves the complete dynamic RBAC configuration matrix.")
    @PreAuthorize("hasAuthority('CATALOG:READ')")
    @GetMapping("/catalog/matrix")
    public ResponseEntity<AuthorityMatrixResponse> getAuthorityMatrix() {
        log.info("REST request to retrieve system authority matrix");
        AuthorityMatrixResponse response = moduleService.getAuthorityMatrix();
        return ResponseEntity.ok(response);
    }
}

