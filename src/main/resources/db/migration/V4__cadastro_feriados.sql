CREATE TABLE feriado (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    data_feriado DATE NOT NULL,
    abrangencia VARCHAR(20) NOT NULL,
    municipio VARCHAR(100),
    uf CHAR(2),
    recorrente_anual BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    observacao VARCHAR(500),
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_feriado_abrangencia CHECK (
        abrangencia IN ('NACIONAL', 'ESTADUAL', 'MUNICIPAL', 'PONTO_FACULTATIVO')
    ),
    CONSTRAINT uq_feriado_data_nome UNIQUE (data_feriado, nome)
);
CREATE INDEX idx_feriado_data_ativo ON feriado (data_feriado, ativo);
