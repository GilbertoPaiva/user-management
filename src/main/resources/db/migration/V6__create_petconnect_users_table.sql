CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    user_type VARCHAR(50) NOT NULL CHECK (user_type IN ('ADMIN', 'LOJISTA', 'TUTOR', 'VETERINARIO')),
    active BOOLEAN NOT NULL DEFAULT true,
    
    security_question_1 TEXT,
    security_answer_1 TEXT,
    security_question_2 TEXT,
    security_answer_2 TEXT,
    security_question_3 TEXT,
    security_answer_3 TEXT,
    
    nome VARCHAR(255),
    location TEXT,
    contact_number VARCHAR(20),
    cnpj VARCHAR(18),
    crmv VARCHAR(50),
    store_type VARCHAR(50),
    business_hours TEXT,
    guardian VARCHAR(255),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_user_type ON users(user_type);
