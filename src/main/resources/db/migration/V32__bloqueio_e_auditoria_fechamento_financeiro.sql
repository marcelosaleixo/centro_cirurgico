CREATE TABLE auditoria_fechamento_financeiro (
 id BIGSERIAL PRIMARY KEY,
 cirurgia_id BIGINT NOT NULL REFERENCES cirurgia(id),
 acao VARCHAR(30) NOT NULL,
 responsavel VARCHAR(120) NOT NULL,
 detalhes TEXT NOT NULL,
 registrado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_auditoria_fin_cirurgia_data ON auditoria_fechamento_financeiro(cirurgia_id, registrado_em DESC);
