# Etapa 68 — Alertas de estoque mínimo

## Entrega
- Tela `/estoque/alertas-minimo` com itens ativos cujo saldo atual está igual ou abaixo do mínimo cadastrado.
- Indicadores de itens em alerta, itens sem saldo e quantidade agregada estimada para alcançar o estoque mínimo.
- Classificação crítica quando o saldo está zerado/negativo e ação de acesso ao formulário de movimentação.
- Atalho incluído no painel de estoque.

## Regras
- O saldo é consultado no estoque geral; item sem registro de saldo é tratado como saldo zero.
- Quantidade sugerida = máximo(estoque mínimo - saldo atual, zero).
- A tela não efetua compra, pedido a fornecedor ou movimentação automaticamente.
- Não há migration nova: utiliza as tabelas `item_estoque` e `estoque_geral` existentes.

## Teste
```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```
Acesse `http://localhost:8080/estoque/alertas-minimo`.

## Observação
O pacote foi verificado quanto à integridade ZIP. A compilação Maven e os testes integrados com PostgreSQL não foram executados neste ambiente.
