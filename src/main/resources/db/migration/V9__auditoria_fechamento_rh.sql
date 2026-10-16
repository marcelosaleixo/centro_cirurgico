CREATE TABLE auditoria_fechamento_rh (
    id BIGSERIAL PRIMARY KEY,
    periodo_inicio DATE NOT NULL,
    periodo_fim DATE NOT NULL,
    colaborador_nome VARCHAR(180),
    acao VARCHAR(40) NOT NULL,
    status_anterior VARCHAR(30),
    status_novo VARCHAR(30),
    detalhe VARCHAR(1000),
    responsavel VARCHAR(120) NOT NULL DEFAULT 'sistema',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_auditoria_fechamento_datas CHECK (periodo_fim >= periodo_inicio)
);
CREATE INDEX idx_auditoria_fechamento_periodo_data
    ON auditoria_fechamento_rh (periodo_inicio, periodo_fim, criado_em DESC);
