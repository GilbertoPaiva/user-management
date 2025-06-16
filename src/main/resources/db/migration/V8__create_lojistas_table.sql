CREATE TABLE IF NOT EXISTS lojistas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(18) UNIQUE,
    location TEXT,
    contact_number VARCHAR(20),
    store_type VARCHAR(50) NOT NULL CHECK (store_type IN ('VIRTUAL', 'LOCAL')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lojistas_user_id ON lojistas(user_id);
CREATE INDEX IF NOT EXISTS idx_lojistas_cnpj ON lojistas(cnpj);
