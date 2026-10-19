CREATE TABLE montagem_kit_mutirao (
 id BIGSERIAL PRIMARY KEY,
 dia_mutirao_id BIGINT NOT NULL REFERENCES dia_mutirao(id),
 kit_id BIGINT NOT NULL REFERENCES kit_cirurgico(id),
 quantidade_kits INTEGER NOT NULL DEFAULT 1 CHECK (quantidade_kits BETWEEN 1 AND 1000),
 status VARCHAR(24) NOT NULL DEFAULT 'PENDENTE',
 responsavel_montagem VARCHAR(160),
 observacoes TEXT,
 criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_montagem_kit_dia ON montagem_kit_mutirao(dia_mutirao_id, criado_em DESC);
CREATE TABLE item_conferencia_kit_mutirao (
 id BIGSERIAL PRIMARY KEY,
 montagem_id BIGINT NOT NULL REFERENCES montagem_kit_mutirao(id) ON DELETE CASCADE,
 item_kit_id BIGINT REFERENCES item_kit_cirurgico(id),
 descricao VARCHAR(160) NOT NULL,
 categoria VARCHAR(20) NOT NULL DEFAULT 'MATERIAL',
 quantidade_prevista NUMERIC(12,3) NOT NULL DEFAULT 1 CHECK (quantidade_prevista > 0),
 quantidade_conferida NUMERIC(12,3),
 unidade VARCHAR(30) NOT NULL DEFAULT 'UNIDADE',
 conferido BOOLEAN NOT NULL DEFAULT FALSE,
 observacao TEXT,
 CHECK (quantidade_conferida IS NULL OR quantidade_conferida >= 0)
);
CREATE INDEX idx_item_conferencia_montagem ON item_conferencia_kit_mutirao(montagem_id);
