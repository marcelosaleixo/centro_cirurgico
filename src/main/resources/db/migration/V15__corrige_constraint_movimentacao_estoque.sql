-- Corrige a validação de origem/destino para permitir ESTOQUE_GERAL sem identificador.
-- Carrinhos e armários continuam exigindo seus respectivos IDs.
ALTER TABLE movimentacao_estoque
    DROP CONSTRAINT IF EXISTS ck_movimentacao_origem_destino;

ALTER TABLE movimentacao_estoque
    ADD CONSTRAINT ck_movimentacao_origem_destino CHECK (
        (
            tipo = 'ENTRADA'
            AND destino_tipo IS NOT NULL
            AND (
                (destino_tipo = 'ESTOQUE_GERAL' AND destino_id IS NULL)
                OR (destino_tipo IN ('CARRINHO', 'ARMARIO') AND destino_id IS NOT NULL)
            )
        )
        OR (
            tipo = 'SAIDA'
            AND origem_tipo IS NOT NULL
            AND (
                (origem_tipo = 'ESTOQUE_GERAL' AND origem_id IS NULL)
                OR (origem_tipo IN ('CARRINHO', 'ARMARIO') AND origem_id IS NOT NULL)
            )
        )
        OR (
            tipo = 'TRANSFERENCIA'
            AND origem_tipo IS NOT NULL
            AND destino_tipo IS NOT NULL
            AND (
                (origem_tipo = 'ESTOQUE_GERAL' AND origem_id IS NULL)
                OR (origem_tipo IN ('CARRINHO', 'ARMARIO') AND origem_id IS NOT NULL)
            )
            AND (
                (destino_tipo = 'ESTOQUE_GERAL' AND destino_id IS NULL)
                OR (destino_tipo IN ('CARRINHO', 'ARMARIO') AND destino_id IS NOT NULL)
            )
        )
    );
