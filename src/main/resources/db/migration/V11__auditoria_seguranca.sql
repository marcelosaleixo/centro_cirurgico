CREATE TABLE auditoria_seguranca (
    id BIGSERIAL PRIMARY KEY,
    ocorrido_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(100) NOT NULL,
    acao VARCHAR(60) NOT NULL,
    detalhes VARCHAR(1000),
    sucesso BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_auditoria_seguranca_data ON auditoria_seguranca (ocorrido_em DESC);
CREATE INDEX idx_auditoria_seguranca_usuario ON auditoria_seguranca (usuario);
