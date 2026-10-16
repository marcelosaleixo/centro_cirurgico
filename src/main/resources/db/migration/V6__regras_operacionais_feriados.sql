ALTER TABLE feriado
    ADD COLUMN regra_operacional VARCHAR(30) NOT NULL DEFAULT 'FUNCIONAMENTO_NORMAL',
    ADD COLUMN percentual_adicional NUMERIC(5,2);

ALTER TABLE feriado
    ADD CONSTRAINT ck_feriado_regra_operacional
        CHECK (regra_operacional IN ('FUNCIONAMENTO_NORMAL', 'ESCALA_ESPECIAL', 'PAGAMENTO_DIFERENCIADO')),
    ADD CONSTRAINT ck_feriado_percentual_adicional
        CHECK (percentual_adicional IS NULL OR percentual_adicional >= 0);
