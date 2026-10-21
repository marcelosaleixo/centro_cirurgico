CREATE TABLE IF NOT EXISTS historico_solicitacao_mutirao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL,
    acao VARCHAR(60) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    responsavel VARCHAR(160),
    status_anterior VARCHAR(24),
    status_novo VARCHAR(24),
    ocorrido_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hist_solicitacao_data
    ON historico_solicitacao_mutirao (solicitacao_id, ocorrido_em DESC);
