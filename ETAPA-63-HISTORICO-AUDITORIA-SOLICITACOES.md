# Etapa 63 — Histórico e auditoria das solicitações de mutirão

## Entrega
- Nova entidade `HistoricoSolicitacaoMutirao` e repositório para persistir movimentações.
- Migration Flyway `V23__historico_auditoria_solicitacoes_mutirao.sql` cria a tabela e índice.
- Registro de criação/edição de rascunho, atualização da conferência de separação e transições de status.
- Linha do tempo na tela de detalhes, ordenada da movimentação mais recente para a mais antiga.
- Cada registro armazena ação, descrição, responsável informado no fluxo, status anterior/novo e data/hora.

## Como testar
1. Faça backup do banco e do projeto antes de atualizar.
2. Extraia o projeto e execute `./mvnw clean test` (Windows: `.\mvnw.cmd clean test`).
3. Inicie com `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`).
4. Crie uma solicitação, edite o rascunho, envie para aprovação, aprove, inicie separação e salve uma conferência.
5. Abra os detalhes da solicitação e confira a seção **Histórico de movimentações**.

## Observações
- A migration cria uma tabela nova; não altera nem apaga os dados atuais.
- O histórico passa a ser registrado a partir desta versão. Não são reconstruídas ações antigas que não tenham sido auditadas.
- O campo responsável contém o nome informado no fluxo atual; esta etapa não substitui esse campo por uma identidade autenticada. Uma etapa posterior pode integrar o usuário autenticado e permissões específicas.
- O histórico registra a conferência como um evento resumido, não um diff individual de cada quantidade.
