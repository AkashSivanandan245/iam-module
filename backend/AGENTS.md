# Xebia LMS - M01 Identity & Access Management
# AGENTS.md

## Implementation Philosophy

- Understand before coding
- Follow contracts-first development
- Prefer clarity over cleverness
- Build incrementally and test each phase
- Keep services small and focused
- Optimize only after correctness

---

## Read Before Anything Else

Read these documents in the exact order before implementing any feature:

1. context/project-overview.md
2. context/architecture.md
3. context/code-standards.md
4. context/library-docs.md
5. context/build-plan.md
6. context/progress-tracker.md

---

# Module Scope

This repository contains only:

```text
M01 - Identity & Access Management & Platform Core
```

Responsibilities:

- Authentication
- JWT RS256
- Dynamic RBAC
- User Management
- Master Data
- Audit Logging
- Transactional Outbox
- Platform Infrastructure

Do NOT implement functionality that belongs to:

```text
M02 Workforce Management

M03 Training Delivery

M04 Course Authoring

M05 Learning Experience

M06 Scheduling

M07 Assessment

M08 Notifications

M09 Frontend

M10 Analytics

M11 Finance
```

Always respect bounded contexts.

---

# Architecture Rules

The project follows a layered architecture:

```text
Controller
↓

Service
↓

Repository
↓

Database
```

Controllers:

- Request handling only
- Validation only
- No business logic

Services:

- Business logic
- Transactions
- Domain rules

Repositories:

- Data access only

---

# Package Structure

Always follow this structure:

```text
com.xebia.lms

├── common
├── config
├── domain
├── dto
│   ├── auth
│   ├── user
│   ├── role
│   ├── catalog
│   └── masterdata
├── mapper
├── repository
├── security
│   └── jwt
├── service
└── web
```

Do not create arbitrary package structures.

---

# Code Standards

Follow:

```text
context/code-standards.md
```

Mandatory rules:

---

## Constructor Injection Only

Allowed:

```java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;

}
```

Forbidden:

```java
@Autowired
private UserRepository repository;
```

---

## No @Data

Allowed:

```java
@Getter
@Setter
@Builder
@RequiredArgsConstructor
```

Forbidden:

```java
@Data
```

---

## DTOs Over Entities

Controllers must return:

```java
UserResponse
```

Never:

```java
AppUser
```

---

## Optional Instead Of Null

Allowed:

```java
Optional<AppUser>
```

Forbidden:

```java
return null;
```

---

## Use Enums

Allowed:

```java
UserStatus.ACTIVE
```

Forbidden:

```java
status == 1
```

---

## Guard Clauses

Allowed:

```java
if (user == null) {
    throw new BadRequestException("User cannot be null");
}
```

Avoid deep nesting.

---

## Logging

Allowed:

```java
log.info(
    "User created: {}",
    userId
);
```

Forbidden:

```java
System.out.println(userId);
```

Never log:

- Passwords
- OTP codes
- JWT tokens
- Secrets
- Private keys

---

# Database Rules

---

## Flyway Only

Schema changes:

```text
V1__schema.sql

V2__seed.sql

V3__...
```

Never modify existing migrations.

---

## No Cross-Module Foreign Keys

Communication between modules must happen through:

```text
REST APIs

Events

IDs
```

Never share tables.

---

## UUID Primary Keys

Preferred:

```java
UUID userId;
```

---

# Security Rules

---

## Deny By Default

No permission means:

```text
ACCESS DENIED
```

Always.

---

## JWT Standard

Use:

```text
RS256
```

Required claims:

```json
{
    "userId": "...",
    "roleId": "...",
    "permissionVersion": 5
}
```

---

## Permission Cache

Redis key:

```text
perm:{userId}:v{permissionVersion}
```

Permission changes must invalidate cache immediately.

---

# Testing Rules

Every feature should include:

---

## Unit Tests

Required for:

```text
Services

Mappers

JWT Logic

Permission Resolution
```

---

## Integration Tests

Required for:

```text
Repositories

Controllers

Flyway Migrations
```

---

# Progress Tracking

After completing a feature:

Update:

```text
context/progress-tracker.md
```

Checklist items must reflect actual implementation status.

---

# Documentation Standards

Every file created in this project must contain meaningful documentation comments.

The goal is to ensure that another developer can understand the purpose, responsibilities, and design decisions of a file without reading its entire implementation.

---

## Class-Level Documentation

Every class must start with a JavaDoc block describing:

- Purpose of the class
- Responsibilities
- Major business rules (if applicable)
- Related components

Example:

```java
/**
 * Service responsible for user lifecycle management.
 *
 * Responsibilities:
 * - Creating users
 * - Updating user details
 * - Assigning roles
 * - Managing user status transitions
 *
 * This service acts as the primary business layer
 * for user-related operations in the IAM module.
 */
@Service
@RequiredArgsConstructor
public class UserService {
}
```

---

## Method-Level Documentation

Public methods must include JavaDoc explaining:

- Purpose
- Parameters
- Return values
- Exceptions thrown
- Important business rules

Example:

```java
/**
 * Creates a new user in the system.
 *
 * Business Rules:
 * - Email must be unique.
 * - Default status is ACTIVE.
 * - Role must exist before assignment.
 *
 * @param request user creation request
 * @return created user response
 * @throws ConflictException if email already exists
 */
public UserResponse createUser(
        CreateUserRequest request
) {
}
```

---

## Inline Comments

Inline comments should explain WHY, not WHAT.

Avoid:

```java
i++; // increment counter
```

Preferred:

```java
// Permission version is incremented so that
// Redis cache entries become immediately invalid.
permissionVersion++;
```

---

## Entity Documentation

Entities should document:

- Business meaning
- Ownership
- Relationships
- Important constraints

Example:

```java
/**
 * Represents a platform user.
 *
 * This entity is owned by the IAM module and serves as
 * the single source of truth for authentication and
 * authorization across the LMS ecosystem.
 */
@Entity
public class AppUser {
}
```

---

## API Documentation

Controllers should use OpenAPI annotations where appropriate.

Example:

```java
@Operation(
    summary = "Create a new user",
    description = "Creates a user and assigns an initial role."
)
@PostMapping
public UserResponse createUser(
        @Valid @RequestBody CreateUserRequest request
) {
}
```

---

## Mandatory Rule

Every newly created file must contain:

✓ Class-level JavaDoc

✓ Public method JavaDoc

✓ Important business-rule comments

✓ Explanations for non-obvious logic

✓ OpenAPI documentation for REST endpoints

Code without proper documentation comments is considered incomplete.

# Build Order

Always follow:

```text
Phase 1

Authentication
↓

User Management

--------------------------------

Phase 2

Roles
Modules
Authorities
Permission Resolution

--------------------------------

Phase 3

Organisation
University
Branch
Domain

--------------------------------

Phase 4

Audit
Outbox
Email
Platform Services
```

Do not skip phases.

---

# Design Principles

Always follow:

---

## SOLID

- Single Responsibility Principle
- Open Closed Principle
- Liskov Substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle

---

## Enterprise Patterns

Use:

```text
Repository Pattern

DTO Pattern

Transactional Outbox

Versioned Permission Cache

Global Exception Handling

Stateless Authentication
```

---

# When Something Breaks

If a problem persists after one correction attempt:

1. Stop implementing
2. Re-read:

```text
architecture.md

code-standards.md

build-plan.md
```

3. Identify architectural violations
4. Fix root causes rather than adding workarounds

---

# Final Goal

The completed module must provide:

```text
Authentication

JWT RS256

Dynamic RBAC

User Management

Master Data APIs

Audit Logging

Transactional Outbox

Global Exception Handling

Swagger Documentation

Flyway Migrations

Docker Deployment
```

This module is the foundation for every other Xebia LMS module and must be implemented with enterprise-grade quality and strict adherence to the official SDD.