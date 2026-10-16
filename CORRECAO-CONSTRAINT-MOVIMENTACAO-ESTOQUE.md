# Correção — constraint de movimentação de estoque

## Causa
A constraint `ck_movimentacao_origem_destino` exigia `destino_id` em toda entrada e IDs para origem/destino de toda saída/transferência. Porém, `ESTOQUE_GERAL` representa o estoque principal e corretamente não possui ID de carrinho/armário (`NULL`). Isso impedia registrar o saldo inicial do item.

## Correção
A migração Flyway `V15__corrige_constraint_movimentacao_estoque.sql` recria a constraint para aceitar `ESTOQUE_GERAL` com ID nulo e continuar exigindo ID para `CARRINHO` e `ARMARIO`. A regra vale para entradas, saídas e transferências.

## Aplicação
1. Faça backup do banco e do projeto.
2. Copie/extraia esta versão sobre uma cópia de trabalho do projeto.
3. Inicie a aplicação para que o Flyway aplique a V15 automaticamente.
4. Teste o cadastro de item com saldo inicial e uma entrada no estoque geral.
5. Execute `./mvnw clean test` (Windows: `.\mvnw.cmd clean test`).

A migração não altera nem apaga movimentações existentes. O ZIP é preparado a partir da Etapa 40; a compilação/testes precisam ser confirmados no ambiente local.
