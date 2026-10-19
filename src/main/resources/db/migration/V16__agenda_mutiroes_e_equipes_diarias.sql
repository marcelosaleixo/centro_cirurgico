-- Etapa 52: evento principal, programação diária e composição da equipe por dia.
CREATE TABLE evento_mutirao (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(160) NOT NULL,
 especialidade VARCHAR(100) NOT NULL,
 data_inicio DATE NOT NULL,
 data_fim DATE NOT NULL,
 responsavel VARCHAR(160),
 observacoes TEXT,
 status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_evento_mutirao_periodo CHECK (data_fim >= data_inicio),
 CONSTRAINT ck_evento_mutirao_status CHECK (status IN ('PLANEJADO','EM_PREPARACAO','EM_ANDAMENTO','CONCLUIDO','CANCELADO'))
);
CREATE INDEX ix_evento_mutirao_periodo ON evento_mutirao(data_inicio,data_fim);
CREATE TABLE dia_mutirao (
 id BIGSERIAL PRIMARY KEY,
 evento_id BIGINT NOT NULL REFERENCES evento_mutirao(id) ON DELETE CASCADE,
 data DATE NOT NULL,
 descricao VARCHAR(120),
 sala_cirurgica_id BIGINT REFERENCES sala_cirurgica(id) ON DELETE RESTRICT,
 status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
 observacoes TEXT,
 CONSTRAINT uk_dia_mutirao_evento_data UNIQUE(evento_id,data),
 CONSTRAINT ck_dia_mutirao_status CHECK(status IN ('PLANEJADO','EM_PREPARACAO','EM_ANDAMENTO','CONCLUIDO','CANCELADO'))
);
CREATE INDEX ix_dia_mutirao_data ON dia_mutirao(data);
CREATE TABLE equipe_dia_mutirao (
 id BIGSERIAL PRIMARY KEY,
 dia_id BIGINT NOT NULL REFERENCES dia_mutirao(id) ON DELETE CASCADE,
 colaborador_id BIGINT NOT NULL REFERENCES colaborador(id) ON DELETE RESTRICT,
 funcao_no_mutirao VARCHAR(100) NOT NULL,
 observacoes TEXT,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT uk_equipe_dia_colaborador_funcao UNIQUE(dia_id,colaborador_id,funcao_no_mutirao)
);
CREATE INDEX ix_equipe_dia_mutirao_dia ON equipe_dia_mutirao(dia_id);
CREATE INDEX ix_equipe_dia_mutirao_colaborador ON equipe_dia_mutirao(colaborador_id);
