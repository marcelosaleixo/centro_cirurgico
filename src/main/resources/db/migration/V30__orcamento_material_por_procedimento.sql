CREATE TABLE IF NOT EXISTS orcamento_material_procedimento (
 id BIGSERIAL PRIMARY KEY,
 procedimento_id BIGINT NOT NULL REFERENCES procedimento(id),
 item_id BIGINT NOT NULL REFERENCES item_estoque(id),
 quantidade_prevista NUMERIC(12,3) NOT NULL CHECK (quantidade_prevista > 0),
 custo_unitario_previsto NUMERIC(14,4) NOT NULL CHECK (custo_unitario_previsto >= 0),
 observacao VARCHAR(500),
 CONSTRAINT uk_orcamento_procedimento_item UNIQUE (procedimento_id,item_id)
);
CREATE INDEX IF NOT EXISTS idx_orcamento_procedimento ON orcamento_material_procedimento(procedimento_id);
