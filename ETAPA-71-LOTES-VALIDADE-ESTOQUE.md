# Etapa 71 — Rastreabilidade por lote e validade

- Cada recebimento de compra registra número do lote e data de validade.
- Cada lote guarda quantidade inicial, saldo do lote e referência à solicitação de compra.
- A tela `/estoque/lotes` lista lotes em ordem de validade e sinaliza vencidos e os que vencem em até 90 dias.
- Migration Flyway `V27__lotes_validade_estoque.sql`.
- A quantidade do lote é registrada junto ao recebimento, no mesmo fluxo transacional que atualiza o saldo geral.

## Observação
Esta etapa registra lotes no recebimento e oferece consulta/alertas. Ainda não implementa baixa automática por lote/FEFO (primeiro a vencer, primeiro a sair); essa é uma evolução separada para evitar inferir o lote consumido sem a seleção explícita.

## Teste
1. Faça backup do projeto e banco.
2. Execute `.\mvnw.cmd clean test`.
3. Registre um pedido como `PEDIDO_REALIZADO` e receba quantidade com número de lote e validade futura.
4. Confira o saldo geral, a movimentação de entrada e a nova linha em `/estoque/lotes`.
