/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.web;

import com.xebia.lms.dto.masterdata.*;
import com.xebia.lms.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Unified Controller managing Master Data (Organisations, Universities, Branches, Domains).
 *
 * All operations require explicit client pre-authorization.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Master Data Management", description = "Endpoints for managing Organisation, University, Branch, and Domain taxonomy details")
public class MasterDataController {

    private final OrganisationService organisationService;
    private final UniversityService universityService;
    private final BranchService branchService;
    private final DomainService domainService;

    // --- Organisation Endpoints ---

    @Operation(summary = "Create organisation", description = "Registers a new corporate or institutional tenant.")
    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/orgs")
    public ResponseEntity<OrganisationResponse> createOrganisation(@Valid @RequestBody CreateOrganisationRequest request) {
        log.info("REST request to create organisation: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.createOrganisation(request));
    }

    @Operation(summary = "Get organisation by ID", description = "Retrieves specific organisation details.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/orgs/{id}")
    public ResponseEntity<OrganisationResponse> getOrganisationById(@PathVariable UUID id) {
        log.info("REST request to fetch organisation: {}", id);
        return ResponseEntity.ok(organisationService.getOrganisationById(id));
    }

    @Operation(summary = "List all organisations", description = "Retrieves all registered organisations.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/orgs")
    public ResponseEntity<List<OrganisationResponse>> getAllOrganisations() {
        log.info("REST request to list all organisations");
        return ResponseEntity.ok(organisationService.getAllOrganisations());
    }

    @Operation(summary = "Update organisation details", description = "Modifies name and description of an existing organisation.")
    @PreAuthorize("hasAuthority('MASTERDATA:UPDATE')")
    @PutMapping("/orgs/{id}")
    public ResponseEntity<OrganisationResponse> updateOrganisation(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateOrganisationRequest request
    ) {
        log.info("REST request to update organisation: {}", id);
        return ResponseEntity.ok(organisationService.updateOrganisation(id, request));
    }

    @Operation(summary = "Delete organisation", description = "Removes organisation. Cascades down to universities and branches.")
    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/orgs/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable UUID id) {
        log.info("REST request to delete organisation: {}", id);
        organisationService.deleteOrganisation(id);
        return ResponseEntity.noContent().build();
    }

    // --- University Endpoints ---

    @Operation(summary = "Create university", description = "Registers a new university under an organisation.")
    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/universities")
    public ResponseEntity<UniversityResponse> createUniversity(@Valid @RequestBody CreateUniversityRequest request) {
        log.info("REST request to create university: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(universityService.createUniversity(request));
    }

    @Operation(summary = "Get university by ID", description = "Retrieves specific university details.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/universities/{id}")
    public ResponseEntity<UniversityResponse> getUniversityById(@PathVariable UUID id) {
        log.info("REST request to fetch university: {}", id);
        return ResponseEntity.ok(universityService.getUniversityById(id));
    }

    @Operation(summary = "List all universities", description = "Retrieves all registered universities.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/universities")
    public ResponseEntity<List<UniversityResponse>> getAllUniversities() {
        log.info("REST request to list all universities");
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @Operation(summary = "List universities by Organisation", description = "Retrieves all universities belonging to a specific organisation.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/universities/org/{orgId}")
    public ResponseEntity<List<UniversityResponse>> getUniversitiesByOrganisationId(@PathVariable UUID orgId) {
        log.info("REST request to list universities for org: {}", orgId);
        return ResponseEntity.ok(universityService.getUniversitiesByOrganisationId(orgId));
    }

    @Operation(summary = "Update university details", description = "Modifies name or organisation mapping of an existing university.")
    @PreAuthorize("hasAuthority('MASTERDATA:UPDATE')")
    @PutMapping("/universities/{id}")
    public ResponseEntity<UniversityResponse> updateUniversity(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUniversityRequest request
    ) {
        log.info("REST request to update university: {}", id);
        return ResponseEntity.ok(universityService.updateUniversity(id, request));
    }

    @Operation(summary = "Delete university", description = "Removes university. Cascades down to branches.")
    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/universities/{id}")
    public ResponseEntity<Void> deleteUniversity(@PathVariable UUID id) {
        log.info("REST request to delete university: {}", id);
        universityService.deleteUniversity(id);
        return ResponseEntity.noContent().build();
    }

    // --- Branch Endpoints ---

    @Operation(summary = "Create branch", description = "Registers a new branch or department under a university.")
    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/branches")
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        log.info("REST request to create branch: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(request));
    }

    @Operation(summary = "Get branch by ID", description = "Retrieves specific branch details.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/branches/{id}")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable UUID id) {
        log.info("REST request to fetch branch: {}", id);
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @Operation(summary = "List all branches", description = "Retrieves all registered branches.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        log.info("REST request to list all branches");
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @Operation(summary = "List branches by University", description = "Retrieves all branches belonging to a specific university.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/branches/university/{uniId}")
    public ResponseEntity<List<BranchResponse>> getBranchesByUniversityId(@PathVariable UUID uniId) {
        log.info("REST request to list branches for university: {}", uniId);
        return ResponseEntity.ok(branchService.getBranchesByUniversityId(uniId));
    }

    @Operation(summary = "Update branch details", description = "Modifies name or university mapping of an existing branch.")
    @PreAuthorize("hasAuthority('MASTERDATA:UPDATE')")
    @PutMapping("/branches/{id}")
    public ResponseEntity<BranchResponse> updateBranch(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateBranchRequest request
    ) {
        log.info("REST request to update branch: {}", id);
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @Operation(summary = "Delete branch", description = "Removes branch.")
    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/branches/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable UUID id) {
        log.info("REST request to delete branch: {}", id);
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    // --- Domain Endpoints ---

    @Operation(summary = "Create domain taxonomy", description = "Registers a new learning domain.")
    @PreAuthorize("hasAuthority('MASTERDATA:CREATE')")
    @PostMapping("/domains")
    public ResponseEntity<DomainResponse> createDomain(@Valid @RequestBody CreateDomainRequest request) {
        log.info("REST request to create domain: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(domainService.createDomain(request));
    }

    @Operation(summary = "Get domain by ID", description = "Retrieves specific learning domain details.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/domains/{id}")
    public ResponseEntity<DomainResponse> getDomainById(@PathVariable UUID id) {
        log.info("REST request to fetch domain: {}", id);
        return ResponseEntity.ok(domainService.getDomainById(id));
    }

    @Operation(summary = "List all domains", description = "Retrieves all registered learning domains.")
    @PreAuthorize("hasAuthority('MASTERDATA:READ')")
    @GetMapping("/domains")
    public ResponseEntity<List<DomainResponse>> getAllDomains() {
        log.info("REST request to list all domains");
        return ResponseEntity.ok(domainService.getAllDomains());
    }

    @Operation(summary = "Update domain details", description = "Modifies name of an existing learning domain.")
    @PreAuthorize("hasAuthority('MASTERDATA:UPDATE')")
    @PutMapping("/domains/{id}")
    public ResponseEntity<DomainResponse> updateDomain(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateDomainRequest request
    ) {
        log.info("REST request to update domain: {}", id);
        return ResponseEntity.ok(domainService.updateDomain(id, request));
    }

    @Operation(summary = "Delete domain", description = "Removes a learning domain taxonomy node.")
    @PreAuthorize("hasAuthority('MASTERDATA:DELETE')")
    @DeleteMapping("/domains/{id}")
    public ResponseEntity<Void> deleteDomain(@PathVariable UUID id) {
        log.info("REST request to delete domain: {}", id);
        domainService.deleteDomain(id);
        return ResponseEntity.noContent().build();
    }
}

