
UPDATE user_roles 
SET role = (SELECT name FROM roles WHERE roles.id = user_roles.role_id)
WHERE role IS NULL OR role = '';

ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_role;
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS pk_user_roles;

ALTER TABLE user_roles DROP COLUMN IF EXISTS role_id;

ALTER TABLE user_roles ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role);

ALTER TABLE user_roles ALTER COLUMN role SET NOT NULL;
