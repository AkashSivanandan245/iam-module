# Progress Tracker

# Xebia LMS - Module 01
## Identity & Access Management (IAM) & Platform Core

---

## Module Objective

Build the foundational platform module responsible for:

- Authentication
- Dynamic RBAC
- User Management
- Master Data Management
- Audit Logging
- API Gateway Support
- Shared Platform Components

This module must be completed before other LMS modules begin implementation.

---

# Overall Progress

| Phase | Status | Progress |
|---------|----------|----------|
| Phase 0: Project Setup | 🟩 Complete | 100% |
| Phase 1: Authentication & User Management | 🟩 Complete | 100% |
| Phase 2: Dynamic RBAC | 🟩 Complete | 100% |
| Phase 3: Master Data Management | 🟩 Complete | 100% |
| Phase 4: Audit & Platform Services | 🟩 Complete | 100% |

---

# Module Requirements Checklist

---

# FR-IAM-01
## Authentication

### Requirements

- [x] User login using email and password
- [x] JWT RS256 access token generation
- [x] Refresh token rotation
- [x] Logout functionality
- [x] Password reset using OTP
- [x] Current user endpoint (`/me`)
- [ ] Enterprise SSO support (Future)

---

## APIs

```http
POST /api/v1/auth/login

POST /api/v1/auth/refresh

POST /api/v1/auth/logout

POST /api/v1/auth/forgot-password

POST /api/v1/auth/verify-otp

POST /api/v1/auth/reset-password

GET /api/v1/auth/me
```

---

## Files

### Domain

```text
AppUser.java
PasswordResetOtp.java
UserStatus.java
```

---

### DTOs

```text
LoginRequest.java
TokenResponse.java
ForgotPasswordRequest.java
VerifyOtpRequest.java
ResetPasswordRequest.java
MeResponse.java
```

---

### Repository

```text
AppUserRepository.java
PasswordResetOtpRepository.java
```

---

### Services

```text
AuthService.java
OtpService.java
```

---

### Security

```text
JwtService.java
JwtAuthenticationFilter.java
JwtKeyProvider.java
TokenStore.java
LmsPrincipal.java
CurrentUser.java
```

---

### Controllers

```text
AuthController.java
```

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-02
## Dynamic RBAC

---

### Requirements

- [x] Runtime-configurable roles
- [x] Runtime-configurable modules
- [x] Dynamic action registration
- [x] Authority generation (MODULE:ACTION)
- [x] User-specific permission overrides
- [x] Deny-by-default access model
- [x] Permission versioning
- [x] Redis permission cache
- [x] Permission invalidation events

---

## APIs

```http
POST /api/v1/roles

GET /api/v1/roles

PUT /api/v1/roles/{id}

PATCH /api/v1/roles/{id}/permissions

POST /api/v1/modules

GET /api/v1/modules

GET /api/v1/authorities
```

---

## Files

---

### Domain

```text
Role.java

Module.java

ActionEntity.java

Authority.java

UserAuthorityOverride.java

OverrideType.java
```

---

### DTOs

```text
CreateRoleRequest.java

UpdateRoleRequest.java

UpdateRolePermissionsRequest.java

RoleResponse.java

CreateModuleRequest.java

ModuleResponse.java

ActionResponse.java

AuthorityResponse.java

AuthorityMatrixResponse.java
```

---

### Repositories

```text
RoleRepository.java

ModuleRepository.java

ActionRepository.java

AuthorityRepository.java

UserAuthorityOverrideRepository.java
```

---

### Mappers

```text
RoleMapper.java

ModuleMapper.java

AuthorityMapper.java
```

---

### Services

```text
RoleService.java

ModuleService.java

PermissionService.java
```

---

### Security

```text
AuthorityResolver.java

RestAccessDeniedHandler.java

RestAuthEntryPoint.java
```

---

### Controllers

```text
RoleController.java

CatalogController.java
```

---

## Business Rules

- [ ] Access denied unless explicitly granted
- [ ] Permission changes effective immediately
- [ ] User login not required after permission updates
- [ ] Redis cache uses permission versioning
- [ ] User overrides take precedence over role permissions

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-03
## User Management

---

### Requirements

- [x] Create users
- [x] Update users
- [x] Change user status
- [x] Assign roles
- [x] View user permissions
- [x] User lifecycle management

---

## APIs

```http
POST /api/v1/users

GET /api/v1/users

GET /api/v1/users/{id}

PUT /api/v1/users/{id}

PATCH /api/v1/users/{id}/status

PATCH /api/v1/users/{id}/role

GET /api/v1/users/{id}/permissions
```

---

## Files

---

### DTOs

```text
CreateUserRequest.java

UpdateUserRequest.java

AssignRoleRequest.java

ChangeStatusRequest.java

UserResponse.java

UserPermissionsResponse.java

RoleSummary.java
```

---

### Mappers

```text
UserMapper.java
```

---

### Services

```text
UserService.java
```

---

### Controllers

```text
UserController.java
```

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-04
## Master Data Management

---

### Requirements

- [x] Organisation CRUD
- [x] University CRUD
- [x] Branch CRUD
- [x] Domain CRUD
- [x] Shared read APIs for other modules

---

## APIs

```http
GET /api/v1/orgs
POST /api/v1/orgs

GET /api/v1/universities
POST /api/v1/universities

GET /api/v1/branches
POST /api/v1/branches

GET /api/v1/domains
POST /api/v1/domains
```

---

## Files

---

### Domain

```text
Organisation.java

University.java

Branch.java

DomainEntity.java
```

---

### DTOs

```text
CreateOrganisationRequest.java
OrganisationResponse.java

CreateUniversityRequest.java
UniversityResponse.java

CreateBranchRequest.java
BranchResponse.java

CreateDomainRequest.java
DomainResponse.java
```

---

### Repositories

```text
OrganisationRepository.java

UniversityRepository.java

BranchRepository.java

DomainRepository.java
```

---

### Mappers

```text
OrganisationMapper.java

UniversityMapper.java

BranchMapper.java

DomainMapper.java
```

---

### Services

```text
OrganisationService.java

UniversityService.java

BranchService.java

DomainService.java
```

---

### Controllers

```text
MasterDataController.java
```

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-05
## Audit Logging

---

### Requirements

- [x] Immutable audit logs
- [x] Authentication event tracking
- [x] User management tracking
- [x] Permission update tracking
- [x] Master data update tracking
- [x] Audit query APIs

---

## Files

---

### Domain

```text
AuditLog.java
```

---

### Repository

```text
AuditLogRepository.java
```

---

### Services

```text
AuditService.java
```

---

## Business Rules

- [x] Audit logs cannot be modified
- [x] Every privileged action is recorded
- [x] Timestamps stored in UTC
- [x] Actor identity must always be captured

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-06
## Transactional Outbox

---

### Requirements

- [x] Business transaction + outbox insert in same transaction
- [x] Reliable event persistence
- [x] Retry support
- [x] Dead-letter support
- [x] Idempotent event processing

---

## Files

---

### Domain

```text
OutboxEvent.java

OutboxStatus.java
```

---

### Repository

```text
OutboxEventRepository.java
```

---

### Services

```text
OutboxService.java
```

---

## Events

---

### permission-changed

```json
{
    "userId": "...",
    "roleId": "...",
    "newVersion": 5
}
```

---

## Business Rules

- [x] Never lose committed events
- [x] Retry failed deliveries
- [x] Dead-letter after maximum attempts
- [x] Consumers must be idempotent

---

## Status

```text
Progress: 100%

Status: COMPLETE
```

---

# FR-IAM-07
## Platform Infrastructure

---

### Requirements

- [x] Global exception handling
- [x] OpenAPI documentation
- [x] Configuration properties
- [x] Docker support
- [x] Flyway migrations
- [x] Standard pagination
- [x] Standard error responses

---

## Files

---

### Common

```text
GlobalExceptionHandler.java

PageResponse.java
```

---

### Exceptions

```text
AuthException.java

BadRequestException.java

ConflictException.java

NotFoundException.java
```

---

### Config

```text
AppProperties.java

OpenApiConfig.java

SecurityConfig.java

S3Config.java
```

---

### Resources

```text
application.yml

application-docker.yml
```

---

### Database

```text
V1__schema.sql

V2__seed.sql
```

---

### DevOps

```text
Dockerfile

docker-compose.yml
```

---

## Status

```text
Progress: 75%

Status: IN PROGRESS
```

---

# Milestone Tracker

---

## Milestone 1
### Authentication Complete

Criteria:

- [x] Login working
- [x] JWT generation working
- [x] Refresh token flow complete
- [x] Password reset complete
- [x] `/me` endpoint implemented

---

## Milestone 2
### Dynamic RBAC Complete

Criteria:

- [x] Roles working
- [x] Modules working
- [x] Authorities generated
- [x] Redis permission cache implemented
- [x] User overrides implemented

---

## Milestone 3
### User Management Complete

Criteria:

- [x] User CRUD complete
- [x] Role assignment complete
- [x] Status management complete
- [x] Permission inspection complete

---

## Milestone 4
### Master Data Complete

Criteria:

- [x] Organisations complete
- [x] Universities complete
- [x] Branches complete
- [x] Domains complete

---

## Milestone 5
### Platform Services Complete

Criteria:

- [ ] Audit logs complete
- [ ] Outbox complete
- [ ] Email infrastructure complete
- [ ] Exception handling complete

---

# Final Completion Criteria

The module is considered complete when:

- [ ] All APIs implemented
- [ ] Flyway migrations complete
- [ ] Swagger documentation complete
- [ ] Unit tests written
- [ ] Integration tests written
- [ ] Docker setup working
- [ ] Security validations complete
- [ ] Code standards followed
- [ ] Architecture document finalized
- [ ] Build plan completed
- [ ] Progress tracker fully checked

---

# Current Module Status

```text
Overall Progress: 80%

Authentication:        🟩
Dynamic RBAC:          🟩
User Management:       🟩
Master Data:           🟩
Audit Logging:         ⬜
Transactional Outbox:  ⬜
Platform Services:     🟨

Module Status:
IN PROGRESS
```