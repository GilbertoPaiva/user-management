CREATE TABLE IF NOT EXISTS servicos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    veterinario_id UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_servicos_veterinario_id ON servicos(veterinario_id);
CREATE INDEX IF NOT EXISTS idx_servicos_nome ON servicos(nome);
