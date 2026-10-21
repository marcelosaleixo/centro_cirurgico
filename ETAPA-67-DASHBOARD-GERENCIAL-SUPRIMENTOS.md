# Etapa 67 — Dashboard gerencial de suprimentos

## Funcionalidades
- Painel gerencial com total de solicitações e somatórios de quantidades solicitadas, separadas e pendentes.
- Distribuição de solicitações por status.
- Contagem de itens com pendência e separação parcial.
- Lista das oito solicitações mais recentes com acesso aos detalhes.
- Atalho no topo da lista de solicitações.

## Acesso
`/mutiroes/dashboard-suprimentos`

## Banco de dados
Não foi adicionada migration: os indicadores são calculados a partir dos registros existentes. Não movimenta estoque automaticamente.

## Validação
Execute `./mvnw clean test` (Linux/macOS) ou `mvnw.cmd clean test` (Windows), depois inicie a aplicação. A compilação e a integração com PostgreSQL não foram executadas durante a geração deste ZIP.
