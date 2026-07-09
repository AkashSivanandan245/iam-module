# M01 - Identity & Access Management (IAM) Architecture

## Module Overview

The Identity & Access Management (IAM) module is the foundational module of the Xebia LMS platform. It is responsible for authentication, authorization, user management, dynamic Role-Based Access Control (RBAC), master data management, and audit logging.

All other modules depend on this module for identity verification and permission resolution.

---

## Responsibilities

### Authentication
- User login using email and password
- JWT (RS256) access token generation
- Refresh token rotation
- Password reset using OTP
- Logout and token invalidation
- Optional SSO integration (future scope)

### Dynamic RBAC
- Runtime configurable modules
- Actions (CREATE, READ, UPDATE, DELETE, etc.)
- Authorities (MODULE:ACTION)
- Roles and permission mapping
- User-specific permission overrides
- Deny-by-default authorization model

### User Management
- User creation and updates
- Role assignment
- Status management
- Permission inspection
- User lifecycle management

### Master Data Management
- Organisation management
- University management
- Branch management
- Domain taxonomy management

### Platform Services
- Immutable audit logging
- Transactional outbox support
- Email notifications
- Global exception handling
- Common response structures

---

# Technology Stack

| Layer | Technology |
|---------|-------------|
| Backend | Spring Boot 3.3 |
| Language | Java 21 |
| Database | PostgreSQL |
| Migrations | Flyway |
| Authentication | JWT RS256 |
| Password Hashing | BCrypt |
| Cache | Redis |
| Object Storage | AWS S3 |
| API Documentation | OpenAPI / Swagger |
| Containerization | Docker |

---

# Architectural Principles

## Layered Architecture

The module follows a standard layered architecture:

```text
Controller (Web Layer)
        ↓
Service Layer
        ↓
Repository Layer
        ↓
Database
```

Responsibilities:

- Controllers handle HTTP requests and responses.
- Services contain business logic.
- Repositories handle data access.
- Entities represent database tables.
- DTOs isolate external contracts from internal models.

---

## SOLID Principles

The implementation follows SOLID principles:

### Single Responsibility Principle (SRP)
Each class has a single responsibility.

Examples:
- AuthService handles authentication.
- RoleService manages roles.
- UserService manages users.
- AuditService handles auditing.

### Open Closed Principle (OCP)
New permissions, modules, and roles can be added without modifying existing code.

### Liskov Substitution Principle (LSP)
Future implementations can replace existing service implementations without affecting clients.

### Interface Segregation Principle (ISP)
Services expose only methods required by consumers.

### Dependency Inversion Principle (DIP)
Controllers depend on service abstractions rather than implementations.

---

# Package Structure

```text
com.xebia.lms
│
├── common
│   ├── exception
│   ├── GlobalExceptionHandler.java
│   └── PageResponse.java
│
├── config
│   ├── AppProperties.java
│   ├── OpenApiConfig.java
│   ├── S3Config.java
│   └── SecurityConfig.java
│
├── domain
│   ├── AppUser.java
│   ├── Role.java
│   ├── Module.java
│   ├── ActionEntity.java
│   ├── Authority.java
│   ├── UserAuthorityOverride.java
│   ├── Organisation.java
│   ├── University.java
│   ├── Branch.java
│   ├── DomainEntity.java
│   ├── AuditLog.java
│   ├── PasswordResetOtp.java
│   ├── OutboxEvent.java
│   └── enums
│
├── dto
│   ├── auth
│   ├── role
│   ├── user
│   ├── catalog
│   └── masterdata
│
├── mapper
│
├── repository
│
├── security
│   └── jwt
│
├── service
│
└── web
```

---

# Domain Model

## AppUser

Represents a platform user.

Attributes:

```text
userId
email
passwordHash
displayName
roleId
organisationId
timezone
status
permissionVersion
createdAt
lastLoginAt
```

---

## Role

Represents a logical role in the system.

Examples:

```text
ADMIN
MANAGER
ORGANISER
TRAINER
LEARNER
```

---

## Module

Represents an application module.

Examples:

```text
IAM
COURSES
ASSESSMENTS
ANALYTICS
SCHEDULING
```

---

## ActionEntity

Represents an operation.

Examples:

```text
CREATE
READ
UPDATE
DELETE
APPROVE
PUBLISH
```

---

## Authority

Represents:

```text
MODULE:ACTION
```

Examples:

```text
USER:CREATE
ROLE:UPDATE
COURSE:PUBLISH
```

---

## UserAuthorityOverride

Allows user-specific permission changes without modifying roles.

Examples:

```text
ALLOW
DENY
```

---

## Master Data Entities

### Organisation

Corporate or institutional tenant.

### University

University hierarchy information.

### Branch

Department or academic branch.

### DomainEntity

Learning domains and categorization.

---

## AuditLog

Immutable record of privileged actions.

Stores:

```text
Actor
Action
Resource
Timestamp
IP Address
Request Metadata
```

---

# Authentication Flow

```text
Client
   ↓
POST /auth/login
   ↓
AuthController
   ↓
AuthService
   ↓
UserRepository
   ↓
BCrypt Password Verification
   ↓
JWT Service
   ↓
Access Token + Refresh Token
   ↓
Response
```

---

# Authorization Flow

The platform follows a deny-by-default RBAC model.

```text
Request
   ↓
JWT Authentication Filter
   ↓
Extract User Information
   ↓
Authority Resolver
   ↓
Redis Cache Lookup
   ↓
Database Fallback
   ↓
Permission Validation
   ↓
Allow / Deny Request
```

---

# Permission Resolution Strategy

Cache key:

```text
perm:{userId}:v{permissionVersion}
```

Example:

```text
perm:123:v5
```

Permission updates:

```text
Grant Permission
    ↓
Increment permissionVersion
    ↓
Old Cache Invalidated
    ↓
New Permissions Loaded Automatically
```

This allows immediate permission propagation without requiring users to log in again.

---

# Database Architecture

The module owns its own schema.

Other modules must:

- Call APIs
- Consume events
- Store references only

No cross-module foreign keys are allowed.

---

# Flyway Migration Strategy

```text
db/migration

V1__schema.sql
V2__seed.sql
```

---

## V1 Schema

Creates:

```text
app_user
role
module
action
authority
user_authority_override

organisation
university
branch
domain

audit_log

password_reset_otp

outbox_event
```

---

## V2 Seed Data

Creates:

```text
ADMIN
MANAGER
ORGANISER
TRAINER
LEARNER

Default Modules
Default Actions
Default Authorities

Super Admin User
```

---

# REST API Structure

## Authentication APIs

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

## User APIs

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

## Role APIs

```http
POST /api/v1/roles

GET /api/v1/roles

PUT /api/v1/roles/{id}

PATCH /api/v1/roles/{id}/permissions
```

---

## Catalog APIs

```http
POST /api/v1/modules

GET /api/v1/modules

GET /api/v1/authorities
```

---

## Master Data APIs

```http
GET /api/v1/orgs

GET /api/v1/universities

GET /api/v1/branches

GET /api/v1/domains
```

---

# Security Components

```text
SecurityConfig
│
├── JwtAuthenticationFilter
├── JwtService
├── JwtKeyProvider
├── AuthorityResolver
├── LmsPrincipal
├── CurrentUser
├── RestAuthEntryPoint
└── RestAccessDeniedHandler
```

---

# Cross-Cutting Components

## Global Exception Handler

Responsible for:

```text
AuthException
BadRequestException
ConflictException
NotFoundException
```

---

## Audit Service

Records:

```text
User Actions
Role Changes
Permission Updates
Master Data Changes
Authentication Events
```

---

## Outbox Pattern

Ensures reliable event delivery.

Flow:

```text
Business Transaction
        +
Outbox Insert
        ↓
Commit
        ↓
Relay Worker
        ↓
Event Bus
```

---

# Design Decisions

| Decision | Reason |
|-----------|----------|
| Layered Architecture | Simplicity and maintainability |
| DTO Pattern | Separation of API contracts from entities |
| JWT RS256 | Enterprise-grade security |
| Redis Permission Cache | Fast authorization checks |
| Permission Versioning | Instant permission updates |
| Flyway Migrations | Controlled schema evolution |
| Transactional Outbox | Reliable event delivery |
| Deny-by-Default RBAC | Secure access model |
| No Cross-Service FK | Independent module ownership |
| Immutable Audit Logs | Compliance and traceability |

---

# Future Enhancements

- Enterprise SSO (OIDC/SAML)
- Redis-backed refresh token store
- Distributed event publishing
- Fine-grained scope permissions
- Multi-factor authentication
- Permission inheritance
- Attribute-Based Access Control (ABAC)

---

# References

- Xebia LMS Enterprise Software Design Document (Version 2.0)
- Module M01: Identity & Access Management and Platform Core
- Cross-Cutting LLD Patterns
- SOLID Principles
- Spring Boot 3.3 Best Practices