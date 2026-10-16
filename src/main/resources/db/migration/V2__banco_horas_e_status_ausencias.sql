-- Permite que a escala represente dias de férias e banco de horas sem remover o histórico existente.
ALTER TABLE escala_plantao
    DROP CONSTRAINT IF EXISTS escala_plantao_status_check;

ALTER TABLE escala_plantao
    ADD CONSTRAINT ck_escala_status
    CHECK (status IN ('PROGRAMADO','REALIZADO','CANCELADO','SUBSTITUIDO','FERIAS','BANCO_HORAS'));

CREATE TABLE banco_horas (
    id BIGSERIAL PRIMARY KEY,
    colaborador_id BIGINT NOT NULL REFERENCES colaborador(id),
    data_banco DATE NOT NULL,
    quantidade_horas NUMERIC(5,2) NOT NULL DEFAULT 12.00
        CHECK (quantidade_horas > 0 AND quantidade_horas <= 24),
    status VARCHAR(15) NOT NULL DEFAULT 'REGISTRADO'
        CHECK (status IN ('REGISTRADO','CANCELADO')),
    motivo VARCHAR(500),
    lancado_por_id BIGINT REFERENCES colaborador(id),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_banco_horas_colaborador_data UNIQUE (colaborador_id, data_banco)
);

CREATE INDEX ix_banco_horas_colaborador_data
    ON banco_horas(colaborador_id, data_banco, status);
