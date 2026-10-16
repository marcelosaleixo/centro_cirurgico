CREATE TABLE auditoria_operacoes (
    id BIGSERIAL PRIMARY KEY,
    ocorrido_em TIMESTAMP WITH TIME ZONE NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    metodo VARCHAR(10) NOT NULL,
    recurso VARCHAR(300) NOT NULL,
    codigo_http INTEGER NOT NULL,
    sucesso BOOLEAN NOT NULL
);

CREATE INDEX idx_auditoria_operacoes_ocorrido_em
    ON auditoria_operacoes (ocorrido_em DESC);
CREATE INDEX idx_auditoria_operacoes_usuario
    ON auditoria_operacoes (usuario);
