-- V2__seed.sql
-- Seed script for Xebia LMS roles, modules, actions, authorities, and initial Super Admin.

-- =========================================================================
-- Seed Roles
-- =========================================================================
INSERT INTO role (id, name, description) VALUES
('d0bcf00e-6e86-4e5b-be4c-0e704de84401', 'ADMIN', 'Super Administrator with full system permissions'),
('d0bcf00e-6e86-4e5b-be4c-0e704de84402', 'MANAGER', 'System manager overseeing courses and trainers'),
('d0bcf00e-6e86-4e5b-be4c-0e704de84403', 'ORGANISER', 'Organiser responsible for schedules and bookings'),
('d0bcf00e-6e86-4e5b-be4c-0e704de84404', 'TRAINER', 'Trainer conducting courses and assessments'),
('d0bcf00e-6e86-4e5b-be4c-0e704de84405', 'LEARNER', 'Learner participating in courses and assessments');

-- =========================================================================
-- Seed Modules
-- =========================================================================
INSERT INTO module (id, name, description) VALUES
('a0e3a6f4-0b19-4c54-94c6-2f0ffb5a3201', 'IAM', 'Identity & Access Management & Platform Core'),
('a0e3a6f4-0b19-4c54-94c6-2f0ffb5a3202', 'COURSES', 'Course Authoring and management'),
('a0e3a6f4-0b19-4c54-94c6-2f0ffb5a3203', 'ASSESSMENTS', 'Tests, quizzes, and gradebooks'),
('a0e3a6f4-0b19-4c54-94c6-2f0ffb5a3204', 'ANALYTICS', 'System reporting and analytics dashboards'),
('a0e3a6f4-0b19-4c54-94c6-2f0ffb5a3205', 'SCHEDULING', 'Class scheduling and trainer allocations');

-- =========================================================================
-- Seed Actions
-- =========================================================================
INSERT INTO action_entity (id, name, description) VALUES
('b0f4c9c1-526d-476c-8f1e-f3b17789f201', 'CREATE', 'Permission to create resources'),
('b0f4c9c1-526d-476c-8f1e-f3b17789f202', 'READ', 'Permission to view/read resources'),
('b0f4c9c1-526d-476c-8f1e-f3b17789f203', 'UPDATE', 'Permission to edit/update resources'),
('b0f4c9c1-526d-476c-8f1e-f3b17789f204', 'DELETE', 'Permission to archive/delete resources'),
('b0f4c9c1-526d-476c-8f1e-f3b17789f205', 'APPROVE', 'Permission to approve workflow tasks'),
('b0f4c9c1-526d-476c-8f1e-f3b17789f206', 'PUBLISH', 'Permission to make content live/published');

-- =========================================================================
-- Generate & Seed Authorities (MODULE:ACTION Cross Product)
-- =========================================================================
INSERT INTO authority (module_id, action_id, authority_name)
SELECT m.id, a.id, m.name || ':' || a.name
FROM module m
CROSS JOIN action_entity a;

-- =========================================================================
-- Map All Authorities to the ADMIN Role
-- =========================================================================
INSERT INTO role_authority (role_id, authority_id)
SELECT 'd0bcf00e-6e86-4e5b-be4c-0e704de84401', id
FROM authority;

-- =========================================================================
-- Seed Super Admin User (password: Admin@123)
-- =========================================================================
INSERT INTO app_user (user_id, email, password_hash, display_name, role_id, status, timezone) VALUES
('e0e1e2e3-e4e5-4e6f-8e7d-8e9da0b1c2d3', 'superadmin@xebia.com', '$2a$12$Zp40oMugRhyjGk/Z7jW5Pey.5w27Z/MspXJqMh1B7z5nCszC5kYKG', 'Super Admin', 'd0bcf00e-6e86-4e5b-be4c-0e704de84401', 'ACTIVE', 'UTC');
