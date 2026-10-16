# Etapa 33 — Auditoria detalhada de equipes, funções e estoque

## Escopo
- Registra diferenças de criação/edição de equipes e funções profissionais.
- Registra exclusões permitidas desses cadastros como evento de auditoria.
- Registra criação, edição e desativação de itens de estoque.
- Registra dados essenciais de movimentações de estoque (item, tipo, quantidade, origem/destino e motivo).
- Reutiliza `AuditoriaAlteracaoService` e a tabela `auditoria_alteracoes` criada na etapa anterior; não adiciona migração Flyway.
- A auditoria não inclui dados pessoais de colaboradores nem credenciais.

## Teste
1. Execute `./mvnw clean test` (Windows: `\mvnw.cmd clean test`).
2. Crie e edite uma equipe e uma função profissional.
3. Crie/edite/desative um item de estoque e registre uma movimentação.
4. Consulte `/admin/auditoria-alteracoes` com usuário `ADMIN_RH`.
5. Verifique os eventos e confirme que valores de movimentação não contêm credenciais.

## Observação
Confirme a compilação e os testes no ambiente do projeto antes de publicar. A auditoria de estoque registra a movimentação operacional, não substituindo o livro de movimentações e os saldos oficiais.
