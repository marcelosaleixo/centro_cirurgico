# Etapa 10 — Regime 5x2 com intervalo

- Colaborador pode usar `DOZE_TRINTA_SEIS` (regime existente) ou `CINCO_POR_DOIS`.
- Regime 5x2 gera escala de segunda a sexta; sábado e domingo são descanso e não geram registro de trabalho.
- Horário padrão: trabalho 07:00–11:00, intervalo 11:00–13:00, trabalho 13:00–17:00.
- A escala passa a armazenar `hora_inicio_intervalo` e `hora_fim_intervalo`.
- Férias e banco de horas continuam avaliados para cada dia previsto de trabalho.
- A regra de referência/paridade 12x36 continua restrita ao regime 12x36.

Flyway aplica `V3__regime_5x2_intervalo_escala.sql`. Não apague o banco.

## Teste
```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Edite um colaborador, selecione o regime 5x2 e gere uma escala semanal. Deve criar registros de segunda a sexta, 07:00–17:00, intervalo 11:00–13:00; sábado e domingo são descanso.
