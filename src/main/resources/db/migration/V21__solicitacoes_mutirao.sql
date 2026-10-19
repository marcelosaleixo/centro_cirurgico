CREATE TABLE solicitacao_mutirao (
 id BIGSERIAL PRIMARY KEY,
 dia_mutirao_id BIGINT NOT NULL REFERENCES dia_mutirao(id),
 status VARCHAR(24) NOT NULL DEFAULT 'RASCUNHO',
 solicitante VARCHAR(160) NOT NULL,
 aprovador VARCHAR(160),
 data_aprovacao TIMESTAMP,
 observacoes TEXT,
 criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_solicitacao_mutirao_dia ON solicitacao_mutirao(dia_mutirao_id);
CREATE INDEX idx_solicitacao_mutirao_status ON solicitacao_mutirao(status);
CREATE TABLE item_solicitacao_mutirao (
 id BIGSERIAL PRIMARY KEY,
 solicitacao_id BIGINT NOT NULL REFERENCES solicitacao_mutirao(id) ON DELETE CASCADE,
 item_estoque_id BIGINT REFERENCES item_estoque(id),
 descricao VARCHAR(160) NOT NULL,
 categoria VARCHAR(20) NOT NULL DEFAULT 'MATERIAL',
 quantidade_solicitada NUMERIC(12,3) NOT NULL CHECK (quantidade_solicitada > 0),
 quantidade_separada NUMERIC(12,3),
 unidade VARCHAR(30) NOT NULL DEFAULT 'UNIDADE',
 observacao TEXT
);
CREATE INDEX idx_item_solicitacao_mutirao_solicitacao ON item_solicitacao_mutirao(solicitacao_id);
