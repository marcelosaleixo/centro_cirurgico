# Etapa 70 — Recebimento de compras integrado ao estoque

- Permite recebimentos parciais ou totais para pedidos no status `PEDIDO_REALIZADO`.
- Registra quantidade acumulada recebida, data/hora e responsável.
- Atualiza o saldo de `estoque_geral` e cria movimentação `ENTRADA` na mesma transação.
- Usa bloqueio pessimista da compra e do saldo para reduzir conflitos de recebimentos simultâneos.
- O status passa a `RECEBIDA` somente quando o total recebido atingir o solicitado.
- Exige saldo previamente cadastrado para o item; não cria saldo automaticamente.
- Migration Flyway: `V26__recebimento_compra_integrado_estoque.sql`.

## Teste
1. Faça backup do projeto e banco.
2. Execute `.\mvnw.cmd clean test` no Windows.
3. Inicie a aplicação e acesse `/compras/solicitacoes`.
4. Marque um pedido como `PEDIDO_REALIZADO`, registre recebimento parcial e depois o restante.
5. Confirme o saldo em `estoque_geral`, a entrada em `movimentacao_estoque` e o status da compra.

A compilação Maven e os testes integrados com PostgreSQL ainda precisam ser executados no ambiente de desenvolvimento.
