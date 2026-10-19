-- Etapa 54: permite vincular uma atribuição de equipe a uma sala cirúrgica específica.
ALTER TABLE equipe_dia_mutirao
    ADD COLUMN sala_cirurgica_id BIGINT NULL REFERENCES sala_cirurgica(id) ON DELETE RESTRICT;
CREATE INDEX ix_equipe_dia_mutirao_sala ON equipe_dia_mutirao(sala_cirurgica_id);
