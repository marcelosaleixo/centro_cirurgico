-- Etapa 75: custo unitário de aquisição por lote para estimativa de custos cirúrgicos.
ALTER TABLE lote_estoque ADD COLUMN IF NOT EXISTS custo_unitario NUMERIC(14,4) NOT NULL DEFAULT 0;
COMMENT ON COLUMN lote_estoque.custo_unitario IS 'Custo de aquisição por unidade do item no lote, em moeda local.';
