CREATE TABLE IF NOT EXISTS veterinarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    crmv VARCHAR(50) UNIQUE,
    location TEXT,
    contact_number VARCHAR(20),
    specialty VARCHAR(100),
    business_hours TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_veterinarios_user_id ON veterinarios(user_id);
CREATE INDEX IF NOT EXISTS idx_veterinarios_crmv ON veterinarios(crmv);
