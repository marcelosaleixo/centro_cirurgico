# Etapa 65 — Relatório de consumo e divergências

## Entrega
- Nova rota `GET /mutiroes/relatorios-consumo`.
- Relatório filtrável por status da solicitação, categoria do item e texto de busca.
- Totais de quantidade solicitada, separada e pendente, e classificação de conferência (COMPLETO, PARCIAL, NÃO SEPARADO).
- Impressão do relatório / salvar como PDF pelo navegador.
- Link de acesso na lista de solicitações.

## Regras
- Quantidade pendente = máximo entre zero e (solicitada - separada).
- Solicitações e itens são apenas consultados; nenhuma movimentação de estoque é realizada.
- Não exige nova migration de banco.

## Testes
Execute `./mvnw clean test` ou `.mvnw.cmd clean test` no Windows e valide a rota com dados reais.
