# Code Standards

# Xebia LMS - M01 Identity & Access Management

---

## Purpose

This document defines the coding standards that must be followed throughout the development of Module 01 (Identity & Access Management).

The standards are based on:

- Xebia Code Writing Standards (21 June 2026)
- Spring Boot Best Practices
- SOLID Principles
- Enterprise Java Development Guidelines

The goal is to ensure:

- Readability
- Maintainability
- Consistency
- Testability
- Scalability
- Team collaboration

---

# 1. Naming Standards

---

## 1.1 Use Intention-Revealing Names

Names should communicate purpose without requiring comments.

### Avoid

```java
int d;

List<User> list;

boolean flag;
```

### Preferred

```java
int elapsedDays;

List<AppUser> activeUsers;

boolean isEmailVerified;
```

---

## 1.2 Java Naming Conventions

---

### Packages

Always lowercase.

```java
com.xebia.lms.user
```

---

### Classes

PascalCase.

```java
UserService

RoleController

JwtAuthenticationFilter
```

---

### Methods

camelCase.

```java
createUser()

assignRole()

validatePermissions()
```

---

### Variables

camelCase.

```java
permissionVersion

activeUsers

refreshToken
```

---

### Constants

UPPER_SNAKE_CASE.

```java
MAX_RETRY_COUNT

DEFAULT_PAGE_SIZE

JWT_EXPIRATION_MINUTES
```

---

## 1.3 Boolean Naming

Booleans should read as questions.

### Avoid

```java
boolean active;

boolean access;
```

---

### Preferred

```java
boolean isActive;

boolean hasPermission;

boolean canBeDeleted;
```

---

# 2. Function Standards

---

## 2.1 Single Responsibility Functions

Each method should perform one task only.

### Avoid

```java
void createUser(CreateUserRequest request) {

    validate(request);

    saveUser(request);

    sendEmail();

    createAuditLog();

}
```

---

### Preferred

```java
void createUser(CreateUserRequest request) {

    validateRequest(request);

    AppUser user = persistUser(request);

    notifyUser(user);

}
```

---

## 2.2 Short Parameter Lists

Methods should accept a maximum of three parameters.

### Avoid

```java
createUser(
    String name,
    String email,
    String phone,
    String city,
    String role
);
```

---

### Preferred

```java
createUser(CreateUserRequest request);
```

---

## 2.3 Guard Clauses

Fail fast and avoid deep nesting.

### Avoid

```java
if (user != null) {

    if (user.isActive()) {

        assignRole(user);

    }

}
```

---

### Preferred

```java
if (user == null) {
    throw new BadRequestException("User cannot be null");
}

if (!user.isActive()) {
    return;
}

assignRole(user);
```

---

# 3. Dependency Management

---

## 3.1 Constructor Injection Only

Field injection is prohibited.

### Avoid

```java
@Autowired
private UserRepository userRepository;
```

---

### Preferred

```java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

}
```

---

## 3.2 Final Dependencies

All dependencies must be immutable.

```java
private final RoleService roleService;

private final AuditService auditService;
```

---

# 4. Constants & Enums

---

## 4.1 Avoid Magic Numbers

### Avoid

```java
if (user.getStatus() == 1)
```

---

### Preferred

```java
if (user.getStatus() == UserStatus.ACTIVE)
```

---

## 4.2 Avoid Hardcoded Strings

### Avoid

```java
if (role.equals("ADMIN"))
```

---

### Preferred

```java
if (role == RoleType.ADMIN)
```

---

## 4.3 Prefer Enums

Examples:

```java
UserStatus

OverrideType

OutboxStatus
```

---

# 5. Null Handling

---

## 5.1 Return Optional Instead of Null

### Avoid

```java
AppUser findByEmail(String email) {

    return null;

}
```

---

### Preferred

```java
Optional<AppUser> findByEmail(String email);
```

---

## 5.2 Use orElseThrow

```java
AppUser user = repository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException("User not found")
        );
```

---

# 6. Immutability

---

## 6.1 Prefer Final Fields

```java
private final UserRepository repository;
```

---

## 6.2 DTOs Should Be Records (Preferred)

Example:

```java
public record CreateUserRequest(

        @NotBlank String name,

        @Email String email

) {
}
```

---

## 6.3 Immutable Objects

Avoid mutable data holders whenever possible.

---

# 7. DTO Standards

---

## 7.1 Never Expose Entities

Controllers must never return JPA entities.

### Avoid

```java
@GetMapping("/{id}")
public AppUser getUser(UUID id)
```

---

### Preferred

```java
@GetMapping("/{id}")
public UserResponse getUser(UUID id)
```

---

## 7.2 Use Dedicated DTO Packages

```text
dto/

├── auth
├── user
├── role
├── catalog
└── masterdata
```

---

## 7.3 Separate Request & Response Models

Example:

```text
CreateUserRequest

UpdateUserRequest

UserResponse
```

---

# 8. Validation Standards

---

## 8.1 Validate at API Boundaries

Validation belongs in DTOs.

### Preferred

```java
public record CreateUserRequest(

        @NotBlank
        String displayName,

        @Email
        String email

) {
}
```

---

## 8.2 Always Use @Valid

```java
@PostMapping
public UserResponse create(

        @Valid
        @RequestBody
        CreateUserRequest request

)
```

---

## 8.3 Avoid Manual Validation

### Avoid

```java
if (request.name() == null)
```

---

# 9. Exception Handling

---

## 9.1 Never Swallow Exceptions

### Avoid

```java
try {

} catch (Exception e) {

}
```

---

### Preferred

```java
try {

    sendEmail();

} catch (EmailException exception) {

    log.error(
        "Failed to send email to {}",
        email,
        exception
    );

    throw new NotificationException(
        "Email delivery failed",
        exception
    );

}
```

---

## 9.2 Use Custom Exceptions

Allowed:

```text
AuthException

BadRequestException

ConflictException

NotFoundException
```

---

## 9.3 Global Exception Handling

All exceptions must be handled through:

```text
GlobalExceptionHandler
```

---

# 10. Logging Standards

---

## 10.1 Never Use System.out

### Prohibited

```java
System.out.println(userId);
```

---

### Preferred

```java
log.info(
    "User created successfully: {}",
    userId
);
```

---

## 10.2 Use Parameterized Logging

### Avoid

```java
log.info("User " + userId);
```

---

### Preferred

```java
log.info(
    "User created: {}",
    userId
);
```

---

## 10.3 Never Log Sensitive Data

Never log:

```text
Passwords

OTP Codes

JWT Tokens

Private Keys

Refresh Tokens

Secret Keys
```

---

# 11. Comments Standards

---

## 11.1 Explain WHY, Not WHAT

---

### Avoid

```java
i++;
```

```java
// Increment i
```

---

### Preferred

```java
// Limited to three retries to avoid
// email gateway rate limits.
```

---

## 11.2 Prefer Self-Documenting Code

Good names eliminate the need for comments.

---

# 12. Formatting Standards

---

## 12.1 Braces

Always use braces.

### Avoid

```java
if (user == null)
    return;
```

---

### Preferred

```java
if (user == null) {
    return;
}
```

---

## 12.2 One Statement Per Line

---

### Avoid

```java
a++; b++;
```

---

### Preferred

```java
a++;

b++;
```

---

## 12.3 Consistent Indentation

Use:

```text
4 spaces
```

Never use tabs.

---

# 13. Spring Boot Standards

---

## 13.1 Layer Responsibilities

---

### Controllers

Responsible for:

```text
Request Handling

Response Mapping

Validation
```

Controllers must not contain business logic.

---

### Services

Responsible for:

```text
Business Logic

Transactions

Validation Rules
```

---

### Repositories

Responsible for:

```text
Database Access Only
```

---

## 13.2 Transaction Management

Use:

```java
@Transactional
```

at the service layer only.

---

## 13.3 Configuration Properties

Always use:

```java
@ConfigurationProperties
```

instead of hardcoded values.

---

# 14. Database Standards

---

## 14.1 Flyway Only

Schema changes must happen through:

```text
V1__schema.sql

V2__seed.sql

V3__...
```

Never modify old migrations.

---

## 14.2 No Cross-Module Foreign Keys

Modules communicate through:

```text
REST APIs

Events

IDs
```

---

## 14.3 Use UUID Primary Keys

Preferred:

```java
UUID userId;
```

---

# 15. Security Standards

---

## 15.1 Deny By Default

Access is denied unless explicitly granted.

---

## 15.2 Constructor Injection Only

Security components must use immutable dependencies.

---

## 15.3 JWT Standards

Algorithm:

```text
RS256
```

Required claims:

```text
userId

roleId

permissionVersion
```

---

# 16. Testing Standards

---

## Unit Tests

Required for:

```text
Services

Mappers

Permission Resolution

JWT Logic
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

## Mocking

Use:

```text
Mockito
```

---

## Database Testing

Use:

```text
Testcontainers
```

---

# 17. Prohibited Practices

The following practices are not allowed:

---

## Dependency Injection

```java
@Autowired
```

---

## Lombok

```java
@Data
```

---

## Console Logging

```java
System.out.println()
```

---

## Null Returns

```java
return null;
```

---

## Empty Catch Blocks

```java
catch (Exception e) {
}
```

---

## Business Logic in Controllers

```java
@PostMapping
public void create() {

    saveUser();

}
```

---

## Entity Exposure

```java
return AppUser;
```

---

## Magic Numbers

```java
status == 1
```

---

# Summary

The Xebia LMS IAM module follows:

- Intention-revealing naming
- Small, focused functions
- Constructor injection
- Immutable objects
- DTO-based APIs
- Optional over null
- Validation at boundaries
- Structured logging
- Global exception handling
- Consistent formatting
- Enterprise Spring Boot practices

Following these standards ensures that the codebase remains clean, maintainable, scalable, and easy for teams to collaborate on.