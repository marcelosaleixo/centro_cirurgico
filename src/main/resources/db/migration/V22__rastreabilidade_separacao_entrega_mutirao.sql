ALTER TABLE solicitacao_mutirao ADD COLUMN IF NOT EXISTS responsavel_separacao VARCHAR(160);
ALTER TABLE solicitacao_mutirao ADD COLUMN IF NOT EXISTS data_separacao TIMESTAMP;
ALTER TABLE solicitacao_mutirao ADD COLUMN IF NOT EXISTS responsavel_entrega VARCHAR(160);
ALTER TABLE solicitacao_mutirao ADD COLUMN IF NOT EXISTS data_entrega TIMESTAMP;
