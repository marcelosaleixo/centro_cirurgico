# Etapa 64 — Auditoria detalhada dos itens

## Entregas
- Entidade `AuditoriaItemSolicitacaoMutirao` para guardar alterações de quantidades por item.
- Repositório com consulta ordenada do histórico por solicitação.
- Migration Flyway `V24__auditoria_detalhada_itens_solicitacao_mutirao.sql`.
- Registro de alterações de `quantidadeSeparada` durante a conferência, preservando valor anterior, valor novo, item, responsável e data/hora.
- Nova tabela na tela de detalhes da solicitação.

## Aplicação
1. Faça backup do banco e do código.
2. Extraia o ZIP e execute `./mvnw clean test` (Windows: `\.\mvnw.cmd clean test`).
3. Inicie a aplicação. Com Flyway habilitado, a migration V24 será aplicada na inicialização.
4. Abra uma solicitação em separação, altere uma quantidade, salve e confirme o histórico detalhado na página de detalhes.

## Observação
O registro desta etapa cobre alterações da quantidade separada. Alterações de descrição, categoria ou quantidade solicitada não são registradas por esta implementação. Os eventos anteriores à migration não são reconstruídos retroativamente.
