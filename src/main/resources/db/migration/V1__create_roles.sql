CREATE TABLE IF NOT EXISTS roles
(
    id         UUID         NOT NULL,
    name       VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

INSERT INTO roles (id, name, created_at, updated_at)
SELECT gen_random_uuid(), 'ROLE_USER', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

INSERT INTO roles (id, name, created_at, updated_at)
SELECT gen_random_uuid(), 'ROLE_ADMIN', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');
