CREATE TABLE auditoria_alteracoes (
    id BIGSERIAL PRIMARY KEY,
    ocorrido_em TIMESTAMPTZ NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    entidade VARCHAR(100) NOT NULL,
    entidade_id VARCHAR(80),
    campo VARCHAR(100) NOT NULL,
    valor_anterior VARCHAR(1000),
    valor_novo VARCHAR(1000),
    operacao VARCHAR(20) NOT NULL
);
CREATE INDEX idx_aud_alt_data ON auditoria_alteracoes (ocorrido_em DESC);
CREATE INDEX idx_aud_alt_entidade ON auditoria_alteracoes (entidade, entidade_id);
