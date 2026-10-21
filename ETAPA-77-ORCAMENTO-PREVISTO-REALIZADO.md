# Etapa 77 — Orçamento previsto versus realizado por procedimento

## Entregas
- Cadastro de orçamento de material por procedimento, com quantidade prevista e custo unitário previsto.
- Validação de quantidade positiva, custo não negativo e unicidade de material por procedimento.
- Comparativo de custo previsto com consumo e custo real estimado a partir dos registros recentes de consumo por lote.
- Migração Flyway V30 para persistir os orçamentos.
- Atalho no menu principal para a gestão de orçamento cirúrgico.

## Rotas
- `GET /orcamentos/cirurgicos`: listar e cadastrar orçamentos.
- `POST /orcamentos/cirurgicos`: salvar orçamento.
- `POST /orcamentos/cirurgicos/{id}/excluir`: excluir orçamento.
- `GET /orcamentos/cirurgicos/comparativo`: visualizar previsto versus realizado.

## Observações importantes
O comparativo atual utiliza os 300 registros de consumo mais recentes, agrupados por procedimento e material, e não um recorte temporal configurável nem uma cirurgia individual. Os custos reais são calculados a partir do custo unitário informado no lote. Antes de usar os resultados para decisões financeiras, valide custos antigos e amplie o comparativo para período selecionável, caso necessário.

Faça backup antes de implantar. A migração V30 deve ser aplicada pelo Flyway. O ZIP foi verificado quanto à integridade; a compilação Maven e os testes integrados precisam ser executados no ambiente de implantação.
