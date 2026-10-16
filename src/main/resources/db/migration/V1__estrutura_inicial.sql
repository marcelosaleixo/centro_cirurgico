CREATE TABLE funcao_profissional (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(100) NOT NULL UNIQUE,
 descricao VARCHAR(255),
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE equipe (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(80) NOT NULL UNIQUE,
 descricao VARCHAR(255),
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE colaborador (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(160) NOT NULL,
 cpf VARCHAR(11) UNIQUE,
 registro_profissional VARCHAR(50),
 email VARCHAR(160) UNIQUE,
 telefone VARCHAR(30),
 funcao_id BIGINT NOT NULL REFERENCES funcao_profissional(id),
 equipe_id BIGINT REFERENCES equipe(id),
 regime_trabalho VARCHAR(20) NOT NULL DEFAULT 'DOZE_TRINTA_SEIS'
   CHECK (regime_trabalho IN ('DOZE_TRINTA_SEIS')),
 plantao_referencia VARCHAR(10) NOT NULL CHECK (plantao_referencia IN ('PAR','IMPAR')),
 data_referencia_plantao DATE NOT NULL,
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE escala_plantao (
 id BIGSERIAL PRIMARY KEY,
 colaborador_id BIGINT NOT NULL REFERENCES colaborador(id),
 data_plantao DATE NOT NULL,
 hora_inicio TIME NOT NULL DEFAULT '07:00',
 hora_fim TIME NOT NULL DEFAULT '19:00',
 tipo_plantao VARCHAR(10) NOT NULL CHECK (tipo_plantao IN ('PAR','IMPAR')),
 status VARCHAR(15) NOT NULL DEFAULT 'PROGRAMADO'
   CHECK (status IN ('PROGRAMADO','REALIZADO','CANCELADO','SUBSTITUIDO')),
 observacao VARCHAR(500),
 CONSTRAINT ck_escala_horario CHECK (hora_inicio <> hora_fim),
 CONSTRAINT uq_escala_colaborador_data UNIQUE (colaborador_id, data_plantao)
);
CREATE TABLE ferias (
 id BIGSERIAL PRIMARY KEY,
 colaborador_id BIGINT NOT NULL REFERENCES colaborador(id),
 data_inicio DATE NOT NULL,
 data_fim DATE NOT NULL,
 status VARCHAR(15) NOT NULL DEFAULT 'SOLICITADA'
   CHECK (status IN ('SOLICITADA','APROVADA','REJEITADA','CANCELADA')),
 aprovado_por_id BIGINT REFERENCES colaborador(id),
 observacao VARCHAR(500),
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_ferias_periodo CHECK (data_fim >= data_inicio)
);
CREATE TABLE troca_plantao (
 id BIGSERIAL PRIMARY KEY,
 solicitante_id BIGINT NOT NULL REFERENCES colaborador(id),
 aceitante_id BIGINT NOT NULL REFERENCES colaborador(id),
 data_plantao_original DATE NOT NULL,
 data_plantao_destino DATE,
 motivo VARCHAR(1000) NOT NULL,
 status_aprovacao VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
   CHECK (status_aprovacao IN ('PENDENTE','APROVADA','REJEITADA','CANCELADA')),
 aprovado_por_id BIGINT REFERENCES colaborador(id),
 data_solicitacao TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 data_decisao TIMESTAMPTZ,
 CONSTRAINT ck_troca_pessoas_diferentes CHECK (solicitante_id <> aceitante_id)
);
CREATE TABLE paciente (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(160) NOT NULL,
 data_nascimento DATE,
 sexo VARCHAR(20),
 prontuario VARCHAR(50) UNIQUE,
 identificador_externo VARCHAR(80) UNIQUE,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE procedimento (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(160) NOT NULL UNIQUE,
 descricao TEXT,
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE sala_cirurgica (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(80) NOT NULL UNIQUE,
 localizacao VARCHAR(160),
 ativa BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE mutirao (
 id BIGSERIAL PRIMARY KEY,
 nome VARCHAR(160) NOT NULL,
 procedimento_id BIGINT NOT NULL REFERENCES procedimento(id),
 data_inicio DATE NOT NULL,
 data_fim DATE NOT NULL,
 status VARCHAR(15) NOT NULL DEFAULT 'PLANEJADO'
   CHECK (status IN ('PLANEJADO','ATIVO','CONCLUIDO','CANCELADO')),
 observacao TEXT,
 CONSTRAINT ck_mutirao_periodo CHECK (data_fim >= data_inicio)
);
CREATE TABLE cirurgia (
 id BIGSERIAL PRIMARY KEY,
 paciente_id BIGINT NOT NULL REFERENCES paciente(id),
 procedimento_id BIGINT NOT NULL REFERENCES procedimento(id),
 medico_responsavel_id BIGINT NOT NULL REFERENCES colaborador(id),
 anestesista_id BIGINT NOT NULL REFERENCES colaborador(id),
 enfermeiro_id BIGINT NOT NULL REFERENCES colaborador(id),
 sala_cirurgica_id BIGINT NOT NULL REFERENCES sala_cirurgica(id),
 mutirao_id BIGINT REFERENCES mutirao(id),
 data_cirurgia DATE NOT NULL,
 hora_inicio TIME NOT NULL,
 hora_fim TIME NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA'
   CHECK (status IN ('AGENDADA','CONFIRMADA','EM_ANDAMENTO','CONCLUIDA','CANCELADA','SUSPENSA')),
 observacao TEXT,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_cirurgia_horario CHECK (hora_fim > hora_inicio)
);
CREATE INDEX ix_cirurgia_data_sala ON cirurgia(data_cirurgia, sala_cirurgica_id);
CREATE INDEX ix_cirurgia_paciente ON cirurgia(paciente_id);
CREATE INDEX ix_escala_data ON escala_plantao(data_plantao);
CREATE INDEX ix_ferias_colaborador_periodo ON ferias(colaborador_id, data_inicio, data_fim);
CREATE TABLE item_estoque (
 id BIGSERIAL PRIMARY KEY,
 codigo VARCHAR(60) NOT NULL UNIQUE,
 nome VARCHAR(160) NOT NULL,
 unidade_medida VARCHAR(20) NOT NULL,
 estoque_minimo NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (estoque_minimo >= 0),
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE estoque_geral (
 id BIGSERIAL PRIMARY KEY,
 item_id BIGINT NOT NULL UNIQUE REFERENCES item_estoque(id),
 quantidade_atual NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
 atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE carrinho_anestesista (
 id BIGSERIAL PRIMARY KEY,
 codigo VARCHAR(60) NOT NULL UNIQUE,
 nome VARCHAR(120) NOT NULL,
 localizacao VARCHAR(160),
 ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE armario_sala (
 id BIGSERIAL PRIMARY KEY,
 sala_cirurgica_id BIGINT NOT NULL REFERENCES sala_cirurgica(id),
 codigo VARCHAR(60) NOT NULL,
 nome VARCHAR(120) NOT NULL,
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT uq_armario_sala_codigo UNIQUE (sala_cirurgica_id, codigo)
);
CREATE TABLE carrinho_item (
 id BIGSERIAL PRIMARY KEY,
 carrinho_id BIGINT NOT NULL REFERENCES carrinho_anestesista(id),
 item_id BIGINT NOT NULL REFERENCES item_estoque(id),
 quantidade_atual NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
 quantidade_minima NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
 CONSTRAINT uq_carrinho_item UNIQUE(carrinho_id, item_id)
);
CREATE TABLE armario_item (
 id BIGSERIAL PRIMARY KEY,
 armario_id BIGINT NOT NULL REFERENCES armario_sala(id),
 item_id BIGINT NOT NULL REFERENCES item_estoque(id),
 quantidade_atual NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantidade_atual >= 0),
 quantidade_minima NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantidade_minima >= 0),
 CONSTRAINT uq_armario_item UNIQUE(armario_id, item_id)
);
CREATE TABLE movimentacao_estoque (
 id BIGSERIAL PRIMARY KEY,
 item_id BIGINT NOT NULL REFERENCES item_estoque(id),
 tipo VARCHAR(15) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA','TRANSFERENCIA')),
 quantidade NUMERIC(12,3) NOT NULL CHECK (quantidade > 0),
 origem_tipo VARCHAR(20) CHECK (origem_tipo IN ('ESTOQUE_GERAL','CARRINHO','ARMARIO')),
 origem_id BIGINT,
 destino_tipo VARCHAR(20) CHECK (destino_tipo IN ('ESTOQUE_GERAL','CARRINHO','ARMARIO')),
 destino_id BIGINT,
 colaborador_id BIGINT REFERENCES colaborador(id),
 data_movimentacao TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 motivo VARCHAR(500),
 CONSTRAINT ck_movimentacao_origem_destino CHECK (
   (tipo = 'ENTRADA' AND destino_tipo IS NOT NULL AND destino_id IS NOT NULL)
   OR (tipo = 'SAIDA' AND origem_tipo IS NOT NULL AND origem_id IS NOT NULL)
   OR (tipo = 'TRANSFERENCIA' AND origem_tipo IS NOT NULL AND origem_id IS NOT NULL
       AND destino_tipo IS NOT NULL AND destino_id IS NOT NULL)
 )
);

-- A troca só é válida entre colaboradores cadastrados na mesma função.
CREATE OR REPLACE FUNCTION validar_funcao_troca_plantao()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE funcao_solicitante BIGINT; funcao_aceitante BIGINT;
BEGIN
 SELECT funcao_id INTO funcao_solicitante FROM colaborador WHERE id = NEW.solicitante_id;
 SELECT funcao_id INTO funcao_aceitante FROM colaborador WHERE id = NEW.aceitante_id;
 IF funcao_solicitante IS NULL OR funcao_aceitante IS NULL
    OR funcao_solicitante <> funcao_aceitante THEN
   RAISE EXCEPTION 'Troca de plantão exige colaboradores da mesma função.';
 END IF;
 RETURN NEW;
END; $$;
CREATE TRIGGER trg_validar_funcao_troca
BEFORE INSERT OR UPDATE OF solicitante_id, aceitante_id ON troca_plantao
FOR EACH ROW EXECUTE FUNCTION validar_funcao_troca_plantao();

-- Impede associar cirurgia a mutirão de procedimento diferente ou fora do período.
CREATE OR REPLACE FUNCTION validar_cirurgia_mutirao()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE proc_mutirao BIGINT; inicio_mutirao DATE; fim_mutirao DATE;
BEGIN
 IF NEW.mutirao_id IS NOT NULL THEN
   SELECT procedimento_id, data_inicio, data_fim
   INTO proc_mutirao, inicio_mutirao, fim_mutirao
   FROM mutirao WHERE id = NEW.mutirao_id;
   IF proc_mutirao IS NULL OR proc_mutirao <> NEW.procedimento_id THEN
     RAISE EXCEPTION 'O procedimento da cirurgia deve ser o mesmo do mutirão.';
   END IF;
   IF NEW.data_cirurgia < inicio_mutirao OR NEW.data_cirurgia > fim_mutirao THEN
     RAISE EXCEPTION 'A data da cirurgia deve estar dentro do período do mutirão.';
   END IF;
 END IF;
 RETURN NEW;
END; $$;
CREATE TRIGGER trg_validar_cirurgia_mutirao
BEFORE INSERT OR UPDATE OF mutirao_id, procedimento_id, data_cirurgia ON cirurgia
FOR EACH ROW EXECUTE FUNCTION validar_cirurgia_mutirao();
