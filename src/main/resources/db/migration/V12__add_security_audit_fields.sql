-- Migração para adicionar campos de auditoria básicos
-- Adiciona apenas versioning para controle de concorrência

-- Tabela users
ALTER TABLE users 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Tabela lojistas
ALTER TABLE lojistas 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Tabela veterinarios
ALTER TABLE veterinarios 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Tabela tutores
ALTER TABLE tutores 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Tabela servicos
ALTER TABLE servicos 
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Tabela tickets (se existir)
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'tickets') THEN
        ALTER TABLE tickets ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;
    END IF;
END $$;

-- Criar tabela para logs de auditoria de segurança
CREATE TABLE security_audit_logs (
    id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    event_description TEXT,
    user_identifier VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN DEFAULT FALSE,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    additional_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0 NOT NULL
);

-- Criar índices para performance
CREATE INDEX idx_security_audit_event_type ON security_audit_logs (event_type);
CREATE INDEX idx_security_audit_user ON security_audit_logs (user_identifier);
CREATE INDEX idx_security_audit_timestamp ON security_audit_logs (event_timestamp);
CREATE INDEX idx_security_audit_ip ON security_audit_logs (ip_address);

-- Criar tabela para controle de tentativas de login
CREATE TABLE login_attempts (
    id UUID PRIMARY KEY,
    user_identifier VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    attempt_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT FALSE,
    blocked_until TIMESTAMP NULL,
    attempt_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0 NOT NULL
);

-- Criar índices para performance
CREATE INDEX idx_login_attempts_user ON login_attempts (user_identifier);
CREATE INDEX idx_login_attempts_ip ON login_attempts (ip_address);
CREATE INDEX idx_login_attempts_timestamp ON login_attempts (attempt_timestamp);
CREATE UNIQUE INDEX uk_login_attempts_user_ip ON login_attempts (user_identifier, ip_address);
