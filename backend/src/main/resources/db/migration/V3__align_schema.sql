-- V2__align_schema.sql
-- Aligns the database schema to the exact specification.

-- =========================================================================
-- DROP DEPRECATED TABLES (Cascading constraints)
-- =========================================================================
DROP TABLE IF EXISTS user_authority_override CASCADE;
DROP TABLE IF EXISTS role_authority CASCADE;
DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS action_entity CASCADE;

-- =========================================================================
-- ALTER EXISTING TABLES
-- =========================================================================

-- role
ALTER TABLE role RENAME COLUMN id TO role_id;
ALTER TABLE role ADD COLUMN is_system BOOLEAN NOT NULL DEFAULT FALSE;

-- module
ALTER TABLE module RENAME COLUMN id TO module_id;
ALTER TABLE module RENAME COLUMN name TO "key";
ALTER TABLE module ADD COLUMN title VARCHAR(120);
ALTER TABLE module ADD COLUMN icon VARCHAR(64);
ALTER TABLE module ADD COLUMN route VARCHAR(120);
ALTER TABLE module ADD COLUMN is_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE module DROP COLUMN description;

-- organisation
ALTER TABLE organisation RENAME COLUMN id TO org_id;
ALTER TABLE organisation ADD COLUMN domain VARCHAR(120);
ALTER TABLE organisation ADD CONSTRAINT org_domain_unique UNIQUE (domain);
ALTER TABLE organisation ADD COLUMN theme_json JSONB;
ALTER TABLE organisation ADD COLUMN updated_at TIMESTAMP;

-- app_user
ALTER TABLE app_user RENAME COLUMN organisation_id TO org_id;
ALTER TABLE app_user ADD COLUMN sso_subject VARCHAR(255);
ALTER TABLE app_user ALTER COLUMN status TYPE VARCHAR(16);

-- audit_log
DROP TABLE audit_log CASCADE;

CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID REFERENCES app_user(user_id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    entity_id VARCHAR(120) NOT NULL,
    details JSONB,
    ip_address VARCHAR(45)
);

-- =========================================================================
-- CREATE NEW TABLES
-- =========================================================================

CREATE TABLE submodule (
    submodule_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES module(module_id) ON DELETE CASCADE,
    "key" VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(120)
);

CREATE TABLE permission (
    permission_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    submodule_id UUID NOT NULL REFERENCES submodule(submodule_id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permission (
    role_id UUID NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(permission_id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_token (
    token_hash VARCHAR(64) PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255)
);
