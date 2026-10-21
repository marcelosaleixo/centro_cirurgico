CREATE TABLE fechamento_financeiro_cirurgia (
 id BIGSERIAL PRIMARY KEY,
 cirurgia_id BIGINT NOT NULL UNIQUE REFERENCES cirurgia(id),
 status VARCHAR(20) NOT NULL DEFAULT 'ABERTO',
 responsavel_abertura VARCHAR(120) NOT NULL,
 responsavel_aprovacao VARCHAR(120),
 observacao_abertura TEXT,
 observacao_aprovacao TEXT,
 aberto_em TIMESTAMP WITH TIME ZONE NOT NULL,
 aprovado_em TIMESTAMP WITH TIME ZONE,
 reaberto_em TIMESTAMP WITH TIME ZONE,
 responsavel_reabertura VARCHAR(120),
 motivo_reabertura TEXT,
 CONSTRAINT ck_fechamento_financeiro_status CHECK (status IN ('ABERTO','APROVADO'))
);
CREATE INDEX ix_fechamento_financeiro_status ON fechamento_financeiro_cirurgia(status);
