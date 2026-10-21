CREATE TABLE IF NOT EXISTS auditoria_item_solicitacao_mutirao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL,
    item_solicitacao_id BIGINT NOT NULL,
    item_descricao VARCHAR(160) NOT NULL,
    campo_alterado VARCHAR(40) NOT NULL,
    valor_anterior NUMERIC(12,3),
    valor_novo NUMERIC(12,3),
    responsavel VARCHAR(160),
    ocorrido_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_aud_item_solicitacao_data
    ON auditoria_item_solicitacao_mutirao (solicitacao_id, ocorrido_em DESC);
CREATE INDEX IF NOT EXISTS idx_aud_item_id
    ON auditoria_item_solicitacao_mutirao (item_solicitacao_id, ocorrido_em DESC);
