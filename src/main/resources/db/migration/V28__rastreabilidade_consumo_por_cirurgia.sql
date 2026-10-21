CREATE TABLE consumo_cirurgia_lote (
    id BIGSERIAL PRIMARY KEY,
    cirurgia_id BIGINT NOT NULL REFERENCES cirurgia(id),
    item_id BIGINT NOT NULL REFERENCES item_estoque(id),
    lote_id BIGINT NOT NULL REFERENCES lote_estoque(id),
    quantidade NUMERIC(12,3) NOT NULL CHECK (quantidade > 0),
    observacao VARCHAR(500) NOT NULL,
    registrado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_consumo_cirurgia_data ON consumo_cirurgia_lote(cirurgia_id, registrado_em);
CREATE INDEX idx_consumo_lote ON consumo_cirurgia_lote(lote_id);
