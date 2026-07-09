-- V5__granular_module_permissions.sql
-- Adds granular permission codes for each future module tab so roles can be
-- granted access independently (e.g. MANAGER sees Courses but not Enrollments).

-- Step 1: New submodules under existing modules or new ones
INSERT INTO module (module_id, "key", title, is_enabled) VALUES
    (gen_random_uuid(), 'ENROLLMENT',  'Enrollment management',   TRUE),
    (gen_random_uuid(), 'ASSESSMENT',  'Assessment management',   TRUE),
    (gen_random_uuid(), 'ANALYTICS',   'Analytics & reporting',   TRUE)
ON CONFLICT ("key") DO NOTHING;

INSERT INTO submodule (submodule_id, module_id, "key", title)
SELECT gen_random_uuid(), m.module_id, sub.key_name, sub.title
FROM module m
JOIN (VALUES
    ('CATALOG',    'COURSE',      'Course catalog'),
    ('ENROLLMENT', 'ENROLLMENT',  'Enrollment operations'),
    ('ASSESSMENT', 'ASSESSMENT',  'Assessment operations'),
    ('ANALYTICS',  'ANALYTICS',   'Analytics operations')
) AS sub(module_key, key_name, title) ON sub.module_key = m."key"
ON CONFLICT ("key") DO NOTHING;

-- Step 2: Granular permission codes
INSERT INTO permission (permission_id, submodule_id, code, description)
SELECT gen_random_uuid(), s.submodule_id, p.code, p.description
FROM submodule s
JOIN (VALUES
    ('COURSE',      'COURSE:READ',       'Browse course catalog and view course details'),
    ('COURSE',      'COURSE:CREATE',     'Create and publish courses'),
    ('COURSE',      'COURSE:UPDATE',     'Edit existing courses and content'),
    ('COURSE',      'COURSE:DELETE',     'Archive or delete courses'),
    ('ENROLLMENT',  'ENROLLMENT:READ',   'View own enrollments and learning progress'),
    ('ENROLLMENT',  'ENROLLMENT:CREATE', 'Enroll in courses'),
    ('ENROLLMENT',  'ENROLLMENT:UPDATE', 'Manage enrollment status'),
    ('ASSESSMENT',  'ASSESSMENT:READ',   'View assessments and own results'),
    ('ASSESSMENT',  'ASSESSMENT:SUBMIT', 'Submit quiz answers and assignments'),
    ('ASSESSMENT',  'ASSESSMENT:MANAGE', 'Create and grade assessments'),
    ('ANALYTICS',   'ANALYTICS:READ',    'View own learning analytics and progress'),
    ('ANALYTICS',   'ANALYTICS:MANAGE',  'View platform-wide analytics and reports')
) AS p(submodule_key, code, description) ON p.submodule_key = s."key"
ON CONFLICT (code) DO NOTHING;

-- Step 3: Grant everything to ADMIN
INSERT INTO role_permission (role_id, permission_id)
SELECT 'd0bcf00e-6e86-4e5b-be4c-0e704de84401', permission_id
FROM permission
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Step 4: Bump permission_version for ADMIN so Redis reloads
UPDATE app_user
SET permission_version = permission_version + 1
WHERE role_id = 'd0bcf00e-6e86-4e5b-be4c-0e704de84401';
