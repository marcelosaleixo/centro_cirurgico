CREATE TABLE conferencia_fechamento_rh (
    id BIGSERIAL PRIMARY KEY,
    periodo_inicio DATE NOT NULL,
    periodo_fim DATE NOT NULL,
    colaborador_id BIGINT NOT NULL REFERENCES colaborador(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    observacao VARCHAR(1000),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_conferencia_rh_status CHECK (status IN ('PENDENTE', 'CONFERIDO', 'DIVERGENCIA')),
    CONSTRAINT ck_conferencia_rh_periodo CHECK (periodo_fim >= periodo_inicio),
    CONSTRAINT uk_conferencia_rh_periodo_colaborador UNIQUE (periodo_inicio, periodo_fim, colaborador_id)
);

CREATE INDEX idx_conferencia_rh_periodo ON conferencia_fechamento_rh (periodo_inicio, periodo_fim);
