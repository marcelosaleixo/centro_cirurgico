-- Alinha o tipo da coluna com a entidade Feriado (String + @Column(length = 2)).
-- Seguro para bancos onde a V4 já criou uf como CHAR(2).
ALTER TABLE feriado
    ALTER COLUMN uf TYPE VARCHAR(2) USING TRIM(uf)::VARCHAR(2);
