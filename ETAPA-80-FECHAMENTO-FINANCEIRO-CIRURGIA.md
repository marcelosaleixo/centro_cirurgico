# Etapa 80 — Fechamento e aprovação financeira por cirurgia

## Entregas
- Registro único de fechamento por cirurgia.
- Estados ABERTO e APROVADO.
- Responsáveis, datas e observações de abertura/aprovação.
- Reabertura de fechamento aprovado somente com motivo registrado.
- Aprovação bloqueada quando não há registros de consumo.
- Tela disponível em `/financeiro/fechamento-cirurgia` e link para auditoria financeira.
- Migração Flyway `V31__fechamento_aprovacao_financeira_cirurgia.sql`.

## Testes recomendados
1. Iniciar a aplicação e confirmar execução da V31.
2. Abrir fechamento para cirurgia existente.
3. Tentar abrir um segundo fechamento para a mesma cirurgia (deve ser impedido).
4. Tentar aprovar sem consumo (deve ser impedido).
5. Aprovar com consumo, depois reabrir informando motivo.
6. Conferir os timestamps e responsáveis persistidos no PostgreSQL.

## Limitações importantes
Os responsáveis são digitados manualmente nesta versão e não são vinculados ao usuário autenticado; não existe ainda controle de permissão por papel para a aprovação. O fechamento não bloqueia a edição de consumos por outras telas. Configure autenticação/autorizações e bloqueio transacional de alterações após aprovação antes de utilizar em produção.
