CREATE TABLE cirurgia_mutirao (
 id BIGSERIAL PRIMARY KEY,
 dia_id BIGINT NOT NULL REFERENCES dia_mutirao(id) ON DELETE CASCADE,
 paciente_id BIGINT NOT NULL REFERENCES paciente(id),
 procedimento_id BIGINT NOT NULL REFERENCES procedimento(id),
 medico_responsavel VARCHAR(160) NOT NULL,
 hora_inicio TIME NOT NULL,
 hora_fim TIME NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
 observacoes TEXT,
 CONSTRAINT ck_cirurgia_mutirao_horario CHECK (hora_fim > hora_inicio)
);
CREATE INDEX ix_cirurgia_mutirao_dia_hora ON cirurgia_mutirao(dia_id,hora_inicio);
