-- V1__schema.sql
-- Foundational schema definition for Xebia LMS IAM and Platform Core.

-- Enable standard UUID generation if not already active
-- Note: gen_random_uuid() is built-in in PostgreSQL 13+ without extensions

-- =========================================================================
-- MODULE: Master Data
-- =========================================================================

CREATE TABLE organisation (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE university (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    organisation_id UUID NOT NULL REFERENCES organisation(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE branch (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    university_id UUID NOT NULL REFERENCES university(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_branch_name_university UNIQUE (name, university_id)
);

CREATE TABLE domain_entity (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================================
-- MODULE: Access Control (RBAC)
-- =========================================================================

CREATE TABLE role (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE module (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE action_entity (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authority (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES module(id) ON DELETE CASCADE,
    action_id UUID NOT NULL REFERENCES action_entity(id) ON DELETE CASCADE,
    authority_name VARCHAR(200) NOT NULL UNIQUE, -- MODULE:ACTION pattern
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_module_action UNIQUE (module_id, action_id)
);

CREATE TABLE role_authority (
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    authority_id UUID NOT NULL REFERENCES authority(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, authority_id)
);

-- =========================================================================
-- MODULE: User Management
-- =========================================================================

CREATE TABLE app_user (
    user_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL REFERENCES role(id),
    organisation_id UUID REFERENCES organisation(id) ON DELETE SET NULL,
    timezone VARCHAR(100) NOT NULL DEFAULT 'UTC',
    status VARCHAR(50) NOT NULL DEFAULT 'INVITED', -- ACTIVE, SUSPENDED, INVITED
    permission_version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

CREATE TABLE user_authority_override (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
    authority_id UUID NOT NULL REFERENCES authority(id) ON DELETE CASCADE,
    override_type VARCHAR(50) NOT NULL, -- ALLOW, DENY
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_authority UNIQUE (user_id, authority_id)
);

CREATE TABLE password_reset_otp (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================================
-- MODULE: Platform & Infrastructure
-- =========================================================================

CREATE TABLE audit_log (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    resource VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(100),
    metadata TEXT
);

CREATE TABLE outbox_event (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, PROCESSED, FAILED
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);
