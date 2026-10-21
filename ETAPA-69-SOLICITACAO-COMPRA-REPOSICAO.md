# Etapa 69 — Solicitação de compra e reposição

## Escopo
- Cadastro de solicitação de compra associada a um item ativo do estoque.
- Quantidade positiva, justificativa e responsável obrigatórios.
- Fornecedor sugerido e observações opcionais.
- Fluxo de status: pendente de aprovação, aprovada, rejeitada, pedido realizado, recebida ou cancelada.
- Lista de acompanhamento e formulários responsivos com os fragmentos visuais existentes.
- Migration Flyway V25; não alterar o saldo do estoque ao aprovar ou marcar como recebida.

## Validação
Executar `./mvnw clean test` e validar no ambiente com PostgreSQL. O status RECEBIDA significa que a solicitação foi marcada como recebida; o saldo só muda após registrar e conferir a entrada no módulo de movimentações.
