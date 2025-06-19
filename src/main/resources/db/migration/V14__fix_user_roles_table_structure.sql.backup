-- V14__fix_user_roles_table_structure.sql
-- Popula a coluna role baseada nos role_id antes de remover role_id

-- Primeiro, popula a coluna role baseada na tabela roles
UPDATE user_roles 
SET role = r.name 
FROM roles r 
WHERE user_roles.role_id = r.id;

-- Remove foreign key constraints
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_role;
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_user;

-- Remove primary key constraint
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS pk_user_roles;

-- Remove a coluna role_id que não é usada
ALTER TABLE user_roles DROP COLUMN IF EXISTS role_id;

-- Torna a coluna role NOT NULL já que é a única usada
ALTER TABLE user_roles ALTER COLUMN role SET NOT NULL;

-- Adiciona nova primary key
ALTER TABLE user_roles ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role);

-- Re-adiciona a foreign key para users
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
