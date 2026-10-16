# Etapa 12 — Trocas de plantão no calendário

## O que foi implementado
- O calendário consulta trocas com data original ou data de destino no mês selecionado.
- Cada troca é exibida no dia original e, quando informada, também no dia de destino.
- O card informa solicitante, aceitante, status e se a data exibida é original ou destino.
- Aprovadas, pendentes, rejeitadas e canceladas têm distinção visual; apenas APROVADA aparece como aprovada pela chefia.
- Com filtro de colaborador, aparecem apenas trocas em que ele é solicitante ou aceitante.
- A troca não modifica silenciosamente a escala original; ela é apresentada como evento separado, para manter rastreabilidade e evitar alterar plantões sem regra explícita.

## Arquivos alterados
- `repository/TrocaPlantaoRepository.java`: consulta por período.
- `web/DiaCalendarioEscala.java`: inclui lista de trocas.
- `controller/EscalaPlantaoController.java`: carrega, filtra e associa trocas aos dias.
- `templates/escalas/calendario.html`: mostra os eventos de troca.
- `static/css/app.css`: estilos dos status.

## Teste
1. Reinicie a aplicação.
2. Abra `/trocas` e confira se há uma troca cadastrada.
3. Para que seja considerada efetivada, o status deve ser `APROVADA`.
4. Abra `/escalas/calendario?mes=2026-10&colaboradorId=1` (ajuste o ID) e confira a data original e a data de destino, quando houver.

Nenhuma migração de banco é necessária.
