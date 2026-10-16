# Etapa 11 — Calendário mensal de escalas

## Funcionalidades
- Visualização mensal por calendário, com semana de segunda a domingo.
- Filtro por colaborador e navegação entre meses.
- Cores por status: programado, férias, banco de horas, cancelado e realizado.
- Exibe horários e intervalo de almoço quando cadastrado.
- Atalho para editar cada escala.
- Dias de fim de semana sem escala aparecem identificados como descanso; a grade é apenas visual e não cria registros novos.
- Link para o calendário na listagem de escalas.

## URL
`/escalas/calendario`

## Teste local
```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Depois acesse `http://localhost:8080/escalas/calendario`.

A validação de compilação precisa ser feita no ambiente local; este pacote foi montado a partir da Etapa 10 sem execução do Maven.
