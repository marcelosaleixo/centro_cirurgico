CREATE TABLE kit_cirurgico (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(140) NOT NULL,
 especialidade VARCHAR(100) NOT NULL,
 descricao TEXT,
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX ix_kit_cirurgico_nome ON kit_cirurgico(nome);
CREATE TABLE item_kit_cirurgico (
 id BIGSERIAL PRIMARY KEY,
 kit_id BIGINT NOT NULL REFERENCES kit_cirurgico(id) ON DELETE CASCADE,
 item_estoque_id BIGINT NULL REFERENCES item_estoque(id) ON DELETE RESTRICT,
 descricao VARCHAR(160) NOT NULL,
 categoria VARCHAR(20) NOT NULL DEFAULT 'MATERIAL',
 quantidade NUMERIC(12,3) NOT NULL CHECK (quantidade > 0),
 unidade VARCHAR(30) NOT NULL DEFAULT 'UNIDADE',
 observacao TEXT
);
CREATE INDEX ix_item_kit_kit ON item_kit_cirurgico(kit_id);
CREATE INDEX ix_item_kit_estoque ON item_kit_cirurgico(item_estoque_id);
