# Etapa 79 — Auditoria financeira por cirurgia

## Objetivo
Disponibilizar um relatório individual por cirurgia, reunindo os consumos por lote, custo estimado dos materiais e comparação com o orçamento cadastrado para o procedimento.

## Funcionalidades
- Seleção de cirurgia pelo identificador, data, paciente e procedimento.
- Resumo com data, paciente, procedimento e quantidade de registros de consumo.
- Comparação por material entre quantidade/custo previstos e quantidade/custo registrados na cirurgia.
- Detalhamento dos lotes, validade, quantidades, custo unitário, custo total e observações.
- Exportação CSV dos registros de consumo da cirurgia selecionada.
- Atalho no menu lateral.

## Acesso
`/relatorios/auditoria-financeira-cirurgia`

## Observações
- O orçamento continua sendo cadastrado por tipo de procedimento e material; não é um orçamento individual da cirurgia.
- O custo real só é exibido como completo quando os registros da cirurgia possuem custo unitário disponível. Sem custo cadastrado, o sistema sinaliza a limitação.
- O relatório usa os registros de consumo vinculados à entidade `Cirurgia` e não inclui automaticamente registros de mutirão que usem outra entidade.
- Validar compilação Maven, execução das migrações e comportamento com PostgreSQL no ambiente de desenvolvimento antes de produção.
