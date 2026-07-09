/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.service;

import static org.junit.jupiter.api.Assertions.*;

import com.xebia.lms.common.exception.ConflictException;
import com.xebia.lms.common.exception.NotFoundException;
import com.xebia.lms.dto.masterdata.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests verifying complete CRUD capabilities and hierarchical mapping constraints
 * for master data (Organisations, Universities, Branches, Domains).
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MasterDataServiceTests {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UniversityService universityService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private DomainService domainService;

    /**
     * Verifies Organisation CRUD operations and uniqueness constraints.
     */
    @Test
    void testOrganisationCrud() {
        String orgName = "ORG-" + UUID.randomUUID();
        CreateOrganisationRequest createReq = new CreateOrganisationRequest(orgName);

        // 1. Create
        OrganisationResponse response = organisationService.createOrganisation(createReq);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(orgName, response.name());

        // 2. Duplicate constraint
        assertThrows(ConflictException.class, () -> organisationService.createOrganisation(createReq));

        // 3. Read
        OrganisationResponse fetched = organisationService.getOrganisationById(response.id());
        assertEquals(orgName, fetched.name());

        // 4. Update
        String updatedName = "UPDATED-" + orgName;
        OrganisationResponse updated = organisationService.updateOrganisation(response.id(), new UpdateOrganisationRequest(updatedName));
        assertEquals(updatedName, updated.name());

        // 5. List All
        List<OrganisationResponse> all = organisationService.getAllOrganisations();
        assertTrue(all.stream().anyMatch(o -> o.id().equals(response.id())));

        // 6. Delete
        organisationService.deleteOrganisation(response.id());
        assertThrows(NotFoundException.class, () -> organisationService.getOrganisationById(response.id()));
    }

    /**
     * Verifies University CRUD operations, parent validation constraints, and unique name constraints.
     */
    @Test
    void testUniversityCrud() {
        // Create parent organisation
        OrganisationResponse org = organisationService.createOrganisation(new CreateOrganisationRequest("ORG-" + UUID.randomUUID()));

        String uniName = "UNI-" + UUID.randomUUID();
        CreateUniversityRequest createReq = new CreateUniversityRequest(uniName, org.id());

        // 1. Create
        UniversityResponse response = universityService.createUniversity(createReq);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(uniName, response.name());
        assertEquals(org.id(), response.organisationId());

        // 2. Parent validation check (invalid Org UUID)
        UUID invalidOrgId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> universityService.createUniversity(new CreateUniversityRequest("UNI-INVALID", invalidOrgId)));

        // 3. Unique name conflict under same organisation
        assertThrows(ConflictException.class, () -> universityService.createUniversity(createReq));

        // 4. Read
        UniversityResponse fetched = universityService.getUniversityById(response.id());
        assertEquals(uniName, fetched.name());

        // 5. List by Org
        List<UniversityResponse> orgList = universityService.getUniversitiesByOrganisationId(org.id());
        assertEquals(1, orgList.size());
        assertEquals(response.id(), orgList.get(0).id());

        // 6. Update
        String updatedName = "UPDATED-" + uniName;
        UniversityResponse updated = universityService.updateUniversity(response.id(), new UpdateUniversityRequest(updatedName, org.id()));
        assertEquals(updatedName, updated.name());

        // 7. Delete
        universityService.deleteUniversity(response.id());
        assertThrows(NotFoundException.class, () -> universityService.getUniversityById(response.id()));
    }

    /**
     * Verifies Branch CRUD operations, unique constraints, and cascades.
     */
    @Test
    void testBranchCrud() {
        // Create parent hierarchy
        OrganisationResponse org = organisationService.createOrganisation(new CreateOrganisationRequest("ORG-" + UUID.randomUUID()));
        UniversityResponse uni = universityService.createUniversity(new CreateUniversityRequest("UNI-" + UUID.randomUUID(), org.id()));

        String branchName = "BRANCH-CSE-" + UUID.randomUUID();
        CreateBranchRequest createReq = new CreateBranchRequest(branchName, uni.id());

        // 1. Create
        BranchResponse response = branchService.createBranch(createReq);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(branchName, response.name());
        assertEquals(uni.id(), response.universityId());

        // 2. Parent validation check
        UUID invalidUniId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> branchService.createBranch(new CreateBranchRequest("BRANCH-INVALID", invalidUniId)));

        // 3. Unique name conflict under same university
        assertThrows(ConflictException.class, () -> branchService.createBranch(createReq));

        // 4. Read
        BranchResponse fetched = branchService.getBranchById(response.id());
        assertEquals(branchName, fetched.name());

        // 5. List by University
        List<BranchResponse> list = branchService.getBranchesByUniversityId(uni.id());
        assertEquals(1, list.size());
        assertEquals(response.id(), list.get(0).id());

        // 6. Update
        String updatedName = "UPDATED-" + branchName;
        BranchResponse updated = branchService.updateBranch(response.id(), new UpdateBranchRequest(updatedName, uni.id()));
        assertEquals(updatedName, updated.name());

        // 7. Delete
        branchService.deleteBranch(response.id());
        assertThrows(NotFoundException.class, () -> branchService.getBranchById(response.id()));
    }

    /**
     * Verifies Learning Domain CRUD operations and uniqueness constraints.
     */
    @Test
    void testDomainCrud() {
        String domainName = "DOMAIN-ENG-" + UUID.randomUUID();
        CreateDomainRequest createReq = new CreateDomainRequest(domainName);

        // 1. Create
        DomainResponse response = domainService.createDomain(createReq);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(domainName, response.name());

        // 2. Duplicate constraint
        assertThrows(ConflictException.class, () -> domainService.createDomain(createReq));

        // 3. Read
        DomainResponse fetched = domainService.getDomainById(response.id());
        assertEquals(domainName, fetched.name());

        // 4. Update
        String updatedName = "UPDATED-" + domainName;
        DomainResponse updated = domainService.updateDomain(response.id(), new UpdateDomainRequest(updatedName));
        assertEquals(updatedName, updated.name());

        // 5. List All
        List<DomainResponse> all = domainService.getAllDomains();
        assertTrue(all.stream().anyMatch(d -> d.id().equals(response.id())));

        // 6. Delete
        domainService.deleteDomain(response.id());
        assertThrows(NotFoundException.class, () -> domainService.getDomainById(response.id()));
    }
}

