-- V4__grant_admin_full_access.sql
-- Seed the permission table (module -> submodule -> permission) with every
-- code the controllers check via @PreAuthorize, grant them all to ADMIN,
-- and bump permission_version so Redis cache reloads.

-- Step 1: modules
INSERT INTO module (module_id, "key", title, is_enabled) VALUES
                                                             (gen_random_uuid(), 'USER',       'User management',           TRUE),
                                                             (gen_random_uuid(), 'ROLE',       'Role management',           TRUE),
                                                             (gen_random_uuid(), 'MASTERDATA', 'Master data',               TRUE),
                                                             (gen_random_uuid(), 'CATALOG',    'Catalog inspection',        TRUE),
                                                             (gen_random_uuid(), 'ADM',        'Administrative operations', TRUE)
    ON CONFLICT ("key") DO NOTHING;

-- Step 2: submodules
INSERT INTO submodule (submodule_id, module_id, "key", title)
SELECT gen_random_uuid(), m.module_id, sub.key_name, sub.title
FROM module m
         JOIN (VALUES
                   ('USER',       'USER',       'User operations'),
                   ('ROLE',       'ROLE',       'Role operations'),
                   ('MASTERDATA', 'MASTERDATA', 'Master data operations'),
                   ('CATALOG',    'CATALOG',    'Catalog operations'),
                   ('ADM',        'AUDIT',      'Audit-log inspection'),
                   ('ADM',        'RBAC',       'RBAC administration')
) AS sub(module_key, key_name, title) ON sub.module_key = m."key"
    ON CONFLICT ("key") DO NOTHING;

-- Step 3: permissions (codes match @PreAuthorize annotations exactly)
INSERT INTO permission (permission_id, submodule_id, code, description)
SELECT gen_random_uuid(), s.submodule_id, p.code, p.description
FROM submodule s
         JOIN (VALUES
                   ('USER',       'USER:CREATE',       'Create users'),
                   ('USER',       'USER:READ',         'View user profiles and lists'),
                   ('USER',       'USER:UPDATE',       'Update user profiles, roles, and status'),
                   ('ROLE',       'ROLE:CREATE',       'Create roles'),
                   ('ROLE',       'ROLE:READ',         'View roles'),
                   ('ROLE',       'ROLE:UPDATE',       'Modify roles and role-permission mappings'),
                   ('ROLE',       'ROLE:DELETE',       'Delete non-system roles'),
                   ('MASTERDATA', 'MASTERDATA:CREATE', 'Create master-data entities'),
                   ('MASTERDATA', 'MASTERDATA:READ',   'View master-data entities'),
                   ('MASTERDATA', 'MASTERDATA:UPDATE', 'Update master-data entities'),
                   ('MASTERDATA', 'MASTERDATA:DELETE', 'Delete master-data entities'),
                   ('CATALOG',    'CATALOG:READ',      'View module/action/authority catalog'),
                   ('AUDIT',      'ADM:AUDIT:VIEW',    'View audit-log entries'),
                   ('RBAC',       'ADM:RBAC:MANAGE',   'Grant or revoke individual permissions')
) AS p(submodule_key, code, description) ON p.submodule_key = s."key"
    ON CONFLICT (code) DO NOTHING;

-- Step 4: grant every permission to ADMIN role
INSERT INTO role_permission (role_id, permission_id)
SELECT 'd0bcf00e-6e86-4e5b-be4c-0e704de84401', permission_id
FROM permission
    ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Step 5: bump permission_version so Redis cache reloads
UPDATE app_user
SET permission_version = permission_version + 1
WHERE role_id = 'd0bcf00e-6e86-4e5b-be4c-0e704de84401';