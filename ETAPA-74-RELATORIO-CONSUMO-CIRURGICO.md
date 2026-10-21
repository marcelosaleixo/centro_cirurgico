# Etapa 74 — Relatório de consumo cirúrgico

## Entregas
- Relatório com filtros opcionais por cirurgia, data inicial e data final.
- Detalhamento por registro, paciente, procedimento, material, lote, validade, quantidade e observação.
- Resumo da quantidade registrada e total de linhas retornadas.
- Exportação CSV compatível com Excel, respeitando os mesmos filtros.
- Link a partir da tela de rastreabilidade.

## Observação sobre custos
O modelo atual de ItemEstoque não contém custo unitário ou histórico de preço. Para não apresentar valores financeiros inventados, esta etapa não calcula custos; a tela informa essa limitação. Uma etapa posterior pode incluir custo de aquisição por lote e regras para custo médio/último custo.

## Rotas
- GET `/relatorios/consumo-cirurgico`
- GET `/relatorios/consumo-cirurgico/exportar.csv`

## Validação
O ZIP foi verificado quanto à integridade. A compilação Maven e os testes integrados com PostgreSQL devem ser executados no ambiente do projeto antes da implantação.
