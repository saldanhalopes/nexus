-- Flyway migration: V1__init_schema.sql
-- Initial schema for Gestão de Treinamentos

-- Departamentos
CREATE TABLE IF NOT EXISTS departamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT,
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cargos
CREATE TABLE IF NOT EXISTS cargos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    departamento_id UUID REFERENCES departamentos(id) ON DELETE SET NULL
);

-- Utilizadores
CREATE TABLE IF NOT EXISTS utilizadores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    matricula VARCHAR(50) NOT NULL UNIQUE,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    nivel_acesso VARCHAR(20) DEFAULT 'COMUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP,
    cargo_id UUID REFERENCES cargos(id) ON DELETE SET NULL,
    departamento_id UUID REFERENCES departamentos(id) ON DELETE SET NULL,
    metadata JSONB
);

-- Treinamentos
CREATE TABLE IF NOT EXISTS treinamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    carga_horaria INTEGER,
    tipo VARCHAR(50),
    nome_plataforma VARCHAR(255),
    link_plataforma VARCHAR(500),
    conteudo_programatico TEXT,
    obrigatorio BOOLEAN DEFAULT FALSE,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    departamento_id UUID REFERENCES departamentos(id) ON DELETE SET NULL
);

-- Registros de Treinamento
CREATE TABLE IF NOT EXISTS registros_treinamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(20) DEFAULT 'PENDENTE',
    data_inicio DATE,
    data_conclusao DATE,
    nota_obtida DOUBLE PRECISION,
    nota_minima DOUBLE PRECISION DEFAULT 70.0,
    certificado_path VARCHAR(500),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utilizador_id UUID REFERENCES utilizadores(id) ON DELETE CASCADE,
    treinamento_id UUID REFERENCES treinamentos(id) ON DELETE CASCADE,
    avaliado_por UUID REFERENCES utilizadores(id) ON DELETE SET NULL
);

-- Matrizes de Competência
CREATE TABLE IF NOT EXISTS matrizes_competencia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    ano_referencia INTEGER,
    competencias JSONB,
    ativa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    departamento_id UUID REFERENCES departamentos(id) ON DELETE SET NULL,
    cargo_id UUID REFERENCES cargos(id) ON DELETE SET NULL,
    criado_por UUID REFERENCES utilizadores(id) ON DELETE SET NULL
);

-- Documentos
CREATE TABLE IF NOT EXISTS documentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(100) NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    versao VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ATIVO',
    data_validade DATE,
    data_expiracao DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    criado_por UUID REFERENCES utilizadores(id) ON DELETE SET NULL,
    departamento_id UUID REFERENCES departamentos(id) ON DELETE SET NULL,
    metadados JSONB
);

-- Audit Trail
CREATE TABLE IF NOT EXISTS audit_trail (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    data_hora_utc TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tabela_afetada VARCHAR(100) NOT NULL,
    registro_id UUID NOT NULL,
    acao VARCHAR(20) NOT NULL,
    valores_antigos JSONB,
    valores_novos JSONB,
    motivo_alteracao VARCHAR(500),
    ip_origem VARCHAR(50),
    user_agent VARCHAR(500),
    utilizador_id UUID REFERENCES utilizadores(id) ON DELETE SET NULL
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_utilizadores_email ON utilizadores(email);
CREATE INDEX IF NOT EXISTS idx_utilizadores_matricula ON utilizadores(matricula);
CREATE INDEX IF NOT EXISTS idx_utilizadores_status ON utilizadores(status);
CREATE INDEX IF NOT EXISTS idx_audit_data_hora ON audit_trail(data_hora_utc);
CREATE INDEX IF NOT EXISTS idx_audit_tabela ON audit_trail(tabela_afetada);
CREATE INDEX IF NOT EXISTS idx_audit_usuario ON audit_trail(utilizador_id);
CREATE INDEX IF NOT EXISTS idx_registros_utilizador ON registros_treinamento(utilizador_id);
CREATE INDEX IF NOT EXISTS idx_registros_treinamento ON registros_treinamento(treinamento_id);

-- Insert default admin user
INSERT INTO departamentos (id, nome, descricao) 
VALUES ('a0000000-0000-0000-0000-000000000001', 'Administração', 'Departamento Administrativo')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO cargos (id, nome, descricao, departamento_id)
SELECT 'b0000000-0000-0000-0000-000000000001', 'Administrador', 'Cargo de Administrador', id
FROM departamentos WHERE nome = 'Administração'
ON CONFLICT DO NOTHING;

INSERT INTO utilizadores (id, matricula, nome_completo, email, senha_hash, status, nivel_acesso, cargo_id, departamento_id)
SELECT 'c0000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrador do Sistema', 'admin@farma.com', 
       '$2a$10$N9qo8uLOkgxG5p6L/vUmXe5hQ3eJiKx5hQ3eJiKx5hQ3eJiKx5hQ3eJ', 'ATIVO', 'ADMINISTRADOR', 
       (SELECT id FROM cargos WHERE nome = 'Administrador'),
       (SELECT id FROM departamentos WHERE nome = 'Administração')
WHERE NOT EXISTS (SELECT 1 FROM utilizadores WHERE email = 'admin@farma.com');

-- Insert sample departments
INSERT INTO departamentos (nome, descricao) VALUES 
('Farmacêutico', 'Departamento de Produção Farmacêutica'),
('Qualidade', 'Departamento de Controle de Qualidade'),
('Pesquisa e Desenvolvimento', 'Departamento de P&D'),
('Recursos Humanos', 'Departamento de RH')
ON CONFLICT (nome) DO NOTHING;