# Etapa 72 — Baixa de estoque por lote com FEFO

- Nova tela `/estoque/baixa-fefo` para registrar consumo/saída do estoque geral.
- Seleciona lotes não vencidos com saldo positivo pela validade crescente (FEFO).
- Bloqueia os registros de lote e o saldo geral durante a operação para reduzir risco de concorrência.
- Distribui a quantidade entre um ou mais lotes, reduz o saldo de cada lote e o saldo geral na mesma transação.
- Registra movimentação `SAIDA` com motivo e detalhamento dos lotes consumidos.
- Recusa a baixa se o saldo geral ou o total disponível em lotes válidos for insuficiente.

## Observação
A etapa mantém o saldo agregado e os lotes sincronizados para baixas feitas por esta tela. Movimentações antigas ou saídas feitas pelas telas genéricas não são automaticamente distribuídas por lote; para rastreabilidade completa, use esta tela para baixas do estoque geral.
