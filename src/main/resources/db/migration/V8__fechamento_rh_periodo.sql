CREATE TABLE fechamento_rh_periodo (
    id BIGSERIAL PRIMARY KEY,
    periodo_inicio DATE NOT NULL,
    periodo_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
    fechado_em TIMESTAMPTZ,
    observacao_fechamento VARCHAR(1000),
    CONSTRAINT ck_fechamento_rh_status CHECK (status IN ('ABERTO', 'FECHADO')),
    CONSTRAINT ck_fechamento_rh_datas CHECK (periodo_fim >= periodo_inicio),
    CONSTRAINT uk_fechamento_rh_periodo UNIQUE (periodo_inicio, periodo_fim),
    CONSTRAINT ck_fechamento_rh_timestamp CHECK ((status = 'ABERTO' AND fechado_em IS NULL) OR (status = 'FECHADO' AND fechado_em IS NOT NULL))
);
