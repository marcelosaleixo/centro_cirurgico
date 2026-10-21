ALTER TABLE solicitacao_compra
    ADD COLUMN quantidade_recebida NUMERIC(12,3) NOT NULL DEFAULT 0,
    ADD COLUMN recebido_em TIMESTAMP NULL,
    ADD COLUMN responsavel_recebimento VARCHAR(160) NULL;

ALTER TABLE solicitacao_compra
    ADD CONSTRAINT ck_solicitacao_compra_qtd_recebida
    CHECK (quantidade_recebida >= 0 AND quantidade_recebida <= quantidade_solicitada);
