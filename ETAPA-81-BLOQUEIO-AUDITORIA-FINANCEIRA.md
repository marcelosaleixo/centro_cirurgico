# Etapa 81 — Bloqueio e trilha de auditoria financeira

## Incluído
- Migração Flyway `V32__bloqueio_e_auditoria_fechamento_financeiro.sql`.
- Registro persistente de abertura, aprovação e reabertura, com data/hora do banco, responsável informado e detalhes.
- Exibição dos últimos 100 eventos no fechamento financeiro da cirurgia.
- Bloqueio de novos consumos pela tela de rastreabilidade quando a cirurgia possui fechamento `APROVADO`; é necessário reabrir com justificativa.

## Validar antes de produção
- Executar a migração e `./mvnw test`/`mvnw.cmd test`.
- Confirmar o bloqueio com fechamento aprovado e a liberação após reabertura.
- Revisar demais rotas de movimentação de estoque que possam registrar saída para cirurgia.
- O responsável continua sendo digitado manualmente; a trilha não substitui auditoria baseada na identidade autenticada.
