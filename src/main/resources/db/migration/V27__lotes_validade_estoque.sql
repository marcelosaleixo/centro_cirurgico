CREATE TABLE lote_estoque (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES item_estoque(id),
    numero_lote VARCHAR(100) NOT NULL,
    data_validade DATE NOT NULL,
    quantidade_inicial NUMERIC(12,3) NOT NULL CHECK (quantidade_inicial > 0),
    quantidade_atual NUMERIC(12,3) NOT NULL CHECK (quantidade_atual >= 0),
    origem_compra_id BIGINT REFERENCES solicitacao_compra(id),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_lote_item_validade ON lote_estoque(item_id, data_validade);
CREATE INDEX idx_lote_validade ON lote_estoque(data_validade);
