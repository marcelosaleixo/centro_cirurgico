-- Adiciona o regime de trabalho 5x2 e o intervalo intrajornada da escala.
ALTER TABLE colaborador
    DROP CONSTRAINT IF EXISTS colaborador_regime_trabalho_check;
ALTER TABLE colaborador
    DROP CONSTRAINT IF EXISTS ck_colaborador_regime_trabalho;
ALTER TABLE colaborador
    ADD CONSTRAINT ck_colaborador_regime_trabalho
    CHECK (regime_trabalho IN ('DOZE_TRINTA_SEIS', 'CINCO_POR_DOIS'));

ALTER TABLE escala_plantao
    ADD COLUMN IF NOT EXISTS hora_inicio_intervalo TIME NULL,
    ADD COLUMN IF NOT EXISTS hora_fim_intervalo TIME NULL;

ALTER TABLE escala_plantao
    DROP CONSTRAINT IF EXISTS ck_escala_intervalo;
ALTER TABLE escala_plantao
    ADD CONSTRAINT ck_escala_intervalo CHECK (
        (hora_inicio_intervalo IS NULL AND hora_fim_intervalo IS NULL)
        OR
        (hora_inicio_intervalo IS NOT NULL AND hora_fim_intervalo IS NOT NULL
         AND hora_inicio_intervalo < hora_fim_intervalo
         AND hora_inicio_intervalo > hora_inicio
         AND hora_fim_intervalo < hora_fim)
    );
