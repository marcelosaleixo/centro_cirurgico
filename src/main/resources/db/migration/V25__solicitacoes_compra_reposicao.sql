CREATE TABLE solicitacao_compra (
    id BIGSERIAL PRIMARY KEY,
    item_estoque_id BIGINT NOT NULL REFERENCES item_estoque(id),
    quantidade_solicitada NUMERIC(12,3) NOT NULL CHECK (quantidade_solicitada > 0),
    justificativa VARCHAR(1000) NOT NULL,
    fornecedor_sugerido VARCHAR(180),
    responsavel_solicitacao VARCHAR(160) NOT NULL,
    observacao VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE_APROVACAO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_solicitacao_compra_status CHECK (status IN ('PENDENTE_APROVACAO','APROVADA','REJEITADA','PEDIDO_REALIZADO','RECEBIDA','CANCELADA'))
);
CREATE INDEX idx_solicitacao_compra_status_data ON solicitacao_compra(status, criado_em DESC);
