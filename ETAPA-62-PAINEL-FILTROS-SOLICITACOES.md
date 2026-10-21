# Etapa 62 — Painel e filtros das solicitações de mutirão

## O que foi implementado
- Indicadores totais: solicitações, rascunhos, itens em separação e solicitações pendentes de entrega.
- Filtro por status do fluxo (RASCUNHO, ENVIADA, APROVADA, EM_SEPARACAO, ENTREGUE).
- Busca por número da solicitação, solicitante ou nome do evento/mutirão.
- Botão para limpar os filtros e estado vazio amigável.
- Mantidas as regras anteriores: não há baixa automática de estoque ao solicitar, aprovar ou entregar.

## Arquivos alterados
- `src/main/java/com/marceloaleixo/centrocirurgico/controller/SolicitacaoMutiraoController.java`
- `src/main/resources/templates/mutiroes/solicitacoes-lista.html`

## Como testar
1. Execute `./mvnw clean test` (Linux/macOS) ou `\.mvnw.cmd clean test` (Windows).
2. Inicie com `\.mvnw.cmd spring-boot:run`.
3. Acesse `/mutiroes/solicitacoes`, pesquise por solicitante ou nome do mutirão e filtre cada status.
4. Confira se os indicadores continuam refletindo o total geral, mesmo quando uma lista está filtrada.

Nenhuma migração de banco de dados é necessária. A compilação/testes não foram executados neste ambiente; validar no ambiente de desenvolvimento.
