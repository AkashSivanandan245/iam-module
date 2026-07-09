# Project Overview

# Xebia LMS - Module 01
## Identity & Access Management (IAM) & Platform Core

---

## Project Description

The Identity & Access Management (IAM) module is the foundational module of the Xebia Enterprise Learning Management System (LMS). It provides authentication, authorization, user management, dynamic role-based access control (RBAC), master data management, and audit logging services that are consumed by every other module in the platform.

This module acts as the single source of truth for platform identities, permissions, and organizational structures, ensuring secure, scalable, and configurable access management across the entire LMS ecosystem.

---

# Business Purpose

The platform serves multiple universities, colleges, organizations, trainers, and learners through a single multi-tenant deployment.

The IAM module addresses the following business needs:

- Centralized identity management
- Runtime-configurable access control
- Immediate permission propagation without redeployment
- Shared organizational master data
- Secure authentication mechanisms
- Immutable audit trails for compliance
- Enterprise-grade security practices

All remaining LMS modules depend on the contracts and APIs exposed by this module.

---

# Module Scope

The following capabilities are part of Module 01:

---

## Authentication

Features:

- Email/password login
- JWT (RS256) access tokens
- Refresh token rotation
- Logout and token invalidation
- Password reset using OTP
- Current user profile retrieval
- Future SSO integration support

---

## Dynamic Role-Based Access Control (RBAC)

Features:

- Runtime-configurable roles
- Configurable application modules
- Action definitions (CREATE, READ, UPDATE, DELETE, etc.)
- Authority generation using MODULE:ACTION conventions
- User-specific permission overrides
- Permission versioning for instant cache invalidation
- Deny-by-default authorization model

---

## User Management

Features:

- User creation and onboarding
- User profile management
- Role assignment
- Status management
- Permission inspection
- User lifecycle operations

---

## Master Data Management

Features:

- Organisation management
- University management
- Branch management
- Domain taxonomy management

These APIs are consumed by other LMS modules to maintain consistency across the platform.

---

## Audit & Platform Services

Features:

- Immutable audit logging
- Transactional outbox pattern
- Email infrastructure
- Global exception handling
- Pagination standards
- Common API response conventions

---

# Technology Stack

| Layer | Technology |
|---------|-------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Database | PostgreSQL |
| Migrations | Flyway |
| Security | Spring Security |
| Authentication | JWT RS256 |
| Password Encryption | BCrypt |
| Cache | Redis |
| Object Storage | AWS S3 |
| API Documentation | OpenAPI / Swagger |
| Containerization | Docker |
| Build Tool | Maven |

---

# Architectural Style

The project follows a layered architecture with clear separation of responsibilities.

```text
Controller Layer
↓
Service Layer
↓
Repository Layer
↓
Database Layer
```

Key architectural principles include:

- SOLID principles
- Dependency Injection
- DTO-based API contracts
- Repository pattern
- Stateless services
- Event-driven extensibility
- Contracts-first development

---

# Project Structure

```text
src/main/java/com/xebia/lms

├── common
│   ├── exception
│   ├── GlobalExceptionHandler
│   └── PageResponse
│
├── config
│
├── domain
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

# Core Domain Models

The IAM module owns the following entities:

---

## Identity & Authentication

```text
AppUser
PasswordResetOtp
UserStatus
```

---

## RBAC

```text
Role
Module
ActionEntity
Authority
UserAuthorityOverride
OverrideType
```

---

## Master Data

```text
Organisation
University
Branch
DomainEntity
```

---

## Platform Services

```text
AuditLog
OutboxEvent
OutboxStatus
```

---

# Security Model

The platform implements a deny-by-default security model.

Users receive access through:

```text
Role
↓

Authorities

↓

User Overrides

↓

Effective Permissions
```

Authorities follow the convention:

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

# Permission Resolution Strategy

Permissions are cached in Redis using versioned keys:

```text
perm:{userId}:v{permissionVersion}
```

Example:

```text
perm:123:v5
```

When permissions are modified:

```text
Permission Change

↓

Increment Version

↓

Cache Invalidated

↓

Next Request Reloads Permissions
```

This allows permission updates to take effect immediately without requiring users to log in again.

---

# Database Ownership

The IAM module owns its own database schema.

Other modules interact through:

- REST APIs
- Events
- Shared contracts

No cross-module foreign keys are allowed.

This ensures:

- Independent deployment
- Horizontal scalability
- Loose coupling
- Clear bounded contexts

---

# API Overview

---

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

POST /api/v1/orgs

GET /api/v1/universities

POST /api/v1/universities

GET /api/v1/branches

POST /api/v1/branches

GET /api/v1/domains

POST /api/v1/domains
```

---

# Development Roadmap

The project will be implemented in four phases:

---

## Phase 1

Authentication & User Management

Deliverables:

- Login
- JWT
- Password Reset
- User CRUD
- Role Assignment

---

## Phase 2

Dynamic RBAC

Deliverables:

- Roles
- Modules
- Actions
- Authorities
- Permission Resolution
- User Overrides

---

## Phase 3

Master Data

Deliverables:

- Organisations
- Universities
- Branches
- Domains

---

## Phase 4

Platform Services

Deliverables:

- Audit Logging
- Transactional Outbox
- Email Infrastructure
- Global Exception Handling

---

# Design Principles

The project follows:

---

## SOLID Principles

- Single Responsibility Principle
- Open Closed Principle
- Liskov Substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle

---

## Enterprise Patterns

- Repository Pattern
- DTO Pattern
- Transactional Outbox Pattern
- Versioned Permission Cache
- Global Exception Handling
- Stateless Authentication
- Event-Driven Architecture

---

# Future Enhancements

Potential future improvements include:

- Enterprise SSO (OIDC/SAML)
- Multi-factor authentication
- Fine-grained attribute-based access control (ABAC)
- Distributed event publishing
- Permission inheritance
- Redis-backed refresh token storage
- External identity provider integration

---

# Expected Outcomes

Upon completion, the IAM module will provide:

- Secure authentication services
- Dynamic RBAC capabilities
- Immediate permission propagation
- Shared master data APIs
- Immutable audit trails
- Enterprise-grade security mechanisms
- Scalable and maintainable architecture

The module will serve as the foundation upon which all remaining Xebia LMS modules are built.