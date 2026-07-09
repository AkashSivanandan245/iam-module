# M01 - Identity & Access Management (IAM)
# Build Plan

## Objective

Build the Identity & Access Management (IAM) module from scratch following the Xebia LMS Enterprise SDD while adhering to enterprise development practices, SOLID principles, layered architecture, and contracts-first development.

The module will provide:

- Authentication
- Dynamic RBAC
- User Management
- Master Data Management
- Audit Logging
- Transactional Outbox Support
- Common Platform Components

This module is the foundation for all remaining LMS modules.

---

# Development Strategy

The module will be implemented incrementally in four phases:

```text
Phase 1
Authentication + User Management

Phase 2
Dynamic RBAC

Phase 3
Master Data Management

Phase 4
Audit + Outbox + Platform Services
```

Each phase builds upon the previous one and delivers independently testable functionality.

---

# Phase 0: Project Setup

## Objective

Create the foundational project structure and infrastructure.

---

## Tasks

### Create Spring Boot Project

Technology stack:

```text
Java 21
Spring Boot 3.3
PostgreSQL
Flyway
Spring Security
JWT RS256
Redis
AWS S3
OpenAPI
Docker
```

---

### Create Package Structure

```text
common
config
domain
dto
mapper
repository
security
service
web
```

---

### Configure Infrastructure

Files:

```text
application.yml

AppProperties.java

SecurityConfig.java

OpenApiConfig.java

S3Config.java
```

---

### Configure Flyway

Create:

```text
V1__schema.sql

V2__seed.sql
```

---

## Deliverables

```text
✓ Application starts successfully
✓ Database connection established
✓ Flyway migrations enabled
✓ Swagger UI available
✓ Docker configuration complete
```

---

# Phase 1: Authentication & User Management

## Objective

Implement authentication and user lifecycle management.

---

# Step 1.1 Domain Models

Create:

```text
AppUser.java

UserStatus.java

PasswordResetOtp.java
```

---

## Features

```text
ACTIVE
SUSPENDED
INVITED
```

---

# Step 1.2 Repositories

Create:

```text
AppUserRepository.java

PasswordResetOtpRepository.java
```

---

# Step 1.3 DTOs

Create:

```text
dto/auth

LoginRequest
TokenResponse
ForgotPasswordRequest
VerifyOtpRequest
ResetPasswordRequest
MeResponse
```

---

```text
dto/user

CreateUserRequest
UpdateUserRequest
AssignRoleRequest
ChangeStatusRequest
UserResponse
UserPermissionsResponse
RoleSummary
```

---

# Step 1.4 Mappers

Create:

```text
UserMapper.java
```

---

# Step 1.5 Security Components

Create:

```text
security/

CurrentUser.java

LmsPrincipal.java

TokenStore.java
```

---

```text
security/jwt/

JwtService.java

JwtKeyProvider.java

JwtAuthenticationFilter.java
```

---

# Step 1.6 Services

Create:

```text
AuthService.java

UserService.java

OtpService.java
```

---

## Features

Authentication:

```text
Login

Refresh Token

Logout

Forgot Password

OTP Verification

Reset Password

Current User
```

---

User Management:

```text
Create User

Update User

Change Status

Assign Role

Get Permissions
```

---

# Step 1.7 Controllers

Create:

```text
AuthController.java

UserController.java
```

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

## Deliverables

```text
✓ User CRUD complete

✓ JWT authentication working

✓ Password reset flow implemented

✓ Role assignment working

✓ Current user endpoint implemented
```

---

# Phase 2: Dynamic RBAC

## Objective

Build a fully dynamic, runtime-configurable RBAC system.

---

# Step 2.1 Domain Models

Create:

```text
Role.java

Module.java

ActionEntity.java

Authority.java

UserAuthorityOverride.java

OverrideType.java
```

---

# Step 2.2 Repositories

Create:

```text
RoleRepository.java

ModuleRepository.java

ActionRepository.java

AuthorityRepository.java

UserAuthorityOverrideRepository.java
```

---

# Step 2.3 DTOs

Create:

```text
dto/role

CreateRoleRequest

UpdateRoleRequest

UpdateRolePermissionsRequest

RoleResponse
```

---

```text
dto/catalog

CreateModuleRequest

ModuleResponse

ActionResponse

AuthorityResponse

AuthorityMatrixResponse
```

---

# Step 2.4 Mappers

Create:

```text
RoleMapper.java

ModuleMapper.java

AuthorityMapper.java
```

---

# Step 2.5 Services

Create:

```text
RoleService.java

ModuleService.java

PermissionService.java
```

---

## Features

Roles:

```text
Create Role

Update Role

Delete Role

Assign Permissions

View Permissions
```

---

Modules:

```text
Create Module

Register Actions

Generate Authorities
```

---

Permissions:

```text
MODULE:ACTION

USER:CREATE

ROLE:UPDATE

COURSE:PUBLISH
```

---

User Overrides:

```text
ALLOW

DENY
```

---

# Step 2.6 Security Components

Create:

```text
AuthorityResolver.java

RestAuthEntryPoint.java

RestAccessDeniedHandler.java
```

---

## Permission Resolution Logic

```text
Request

↓

JWT Authentication

↓

Authority Resolver

↓

Redis Cache

↓

Database Fallback

↓

Permission Validation

↓

Allow / Deny
```

---

## Cache Strategy

```text
perm:{userId}:v{permissionVersion}
```

Example:

```text
perm:123:v4
```

---

# Step 2.7 Controllers

Create:

```text
RoleController.java

CatalogController.java
```

---

## APIs

```http
POST /api/v1/roles

GET /api/v1/roles

PUT /api/v1/roles/{id}

PATCH /api/v1/roles/{id}/permissions
```

---

```http
POST /api/v1/modules

GET /api/v1/modules

GET /api/v1/authorities
```

---

## Deliverables

```text
✓ Dynamic RBAC working

✓ Runtime permission updates

✓ Permission versioning

✓ User-specific overrides

✓ Deny-by-default security model
```

---

# Phase 3: Master Data Management

## Objective

Build shared master-data services used across all modules.

---

# Step 3.1 Domain Models

Create:

```text
Organisation.java

University.java

Branch.java

DomainEntity.java
```

---

# Step 3.2 Repositories

Create:

```text
OrganisationRepository.java

UniversityRepository.java

BranchRepository.java

DomainRepository.java
```

---

# Step 3.3 DTOs

Create:

```text
dto/masterdata

CreateOrganisationRequest
OrganisationResponse

CreateUniversityRequest
UniversityResponse

CreateBranchRequest
BranchResponse

CreateDomainRequest
DomainResponse
```

---

# Step 3.4 Mappers

Create:

```text
OrganisationMapper.java

UniversityMapper.java

BranchMapper.java

DomainMapper.java
```

---

# Step 3.5 Services

Create:

```text
OrganisationService.java

UniversityService.java

BranchService.java

DomainService.java
```

---

# Step 3.6 Controllers

Create:

```text
MasterDataController.java
```

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

## Deliverables

```text
✓ Organisation management

✓ University management

✓ Branch management

✓ Domain taxonomy management

✓ Shared APIs for other modules
```

---

# Phase 4: Audit & Platform Services

## Objective

Build cross-cutting platform capabilities.

---

# Step 4.1 Audit Logging

Create:

```text
AuditLog.java

AuditLogRepository.java

AuditService.java
```

---

## Features

Track:

```text
User Actions

Role Changes

Permission Updates

Authentication Events

Master Data Changes
```

---

# Step 4.2 Transactional Outbox

Create:

```text
OutboxEvent.java

OutboxStatus.java

OutboxEventRepository.java

OutboxService.java
```

---

## Workflow

```text
Business Transaction

↓

Outbox Insert

↓

Database Commit

↓

Relay Worker

↓

Event Bus
```

---

# Step 4.3 Email Infrastructure

Create:

```text
EmailSender.java

LoggingEmailSender.java
```

---

## Future Integrations

```text
SMTP

SES

SendGrid
```

---

# Step 4.4 Exception Handling

Create:

```text
AuthException.java

BadRequestException.java

ConflictException.java

NotFoundException.java
```

---

Global handler:

```text
GlobalExceptionHandler.java
```

---

# Standard Error Response

```json
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "User not found",
    "traceId": "abc123",
    "details": {}
  }
}
```

---

## Deliverables

```text
✓ Immutable audit logging

✓ Transactional outbox support

✓ Email infrastructure

✓ Standardized error handling

✓ Platform-wide utilities
```

---

# Testing Strategy

---

## Unit Tests

Target:

```text
Service Layer

Mappers

Permission Resolution

JWT Logic
```

Coverage:

```text
80%+
```

---

## Integration Tests

Target:

```text
Repositories

Controllers

Authentication Flow

Flyway Migrations
```

---

## Security Tests

Target:

```text
Unauthorized Requests

Invalid JWTs

Permission Escalation

Role Overrides

Deny-by-default Behavior
```

---

# Build Order Summary

```text
PHASE 0
Infrastructure Setup
↓
PHASE 1
Authentication
User Management
↓
PHASE 2
Dynamic RBAC
↓
PHASE 3
Master Data
↓
PHASE 4
Audit
Outbox
Email
Platform Services
```

---

# Final Deliverables

```text
Authentication System

JWT RS256 Security

User Management

Dynamic RBAC

Master Data APIs

Audit Logging

Transactional Outbox

Global Exception Handling

Swagger Documentation

Docker Deployment

Flyway Database Migrations
```

---

# Success Criteria

The module will be considered complete when:

```text
✓ Users can authenticate successfully

✓ Roles and permissions can be modified at runtime

✓ Permission changes take effect immediately

✓ Master data APIs are available

✓ Audit logs are immutable

✓ Events are reliably persisted

✓ APIs follow enterprise conventions

✓ Security follows deny-by-default principles

✓ Database migrations are fully automated

✓ All core functionality is independently testable
```