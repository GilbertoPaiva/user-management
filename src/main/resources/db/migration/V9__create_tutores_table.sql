CREATE TABLE IF NOT EXISTS tutores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(18),
    location TEXT,
    contact_number VARCHAR(20),
    guardian VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tutores_user_id ON tutores(user_id);
