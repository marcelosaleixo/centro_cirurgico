# Correção — LazyInitializationException em solicitações de mutirão

## Causa
A tela `mutiroes/solicitacao-formulario.html` exibe `d.evento.nome`, mas a consulta original carregava `DiaMutirao` sem carregar a associação `evento`. Como a associação é `LAZY`, o Thymeleaf tentava acessá-la após o encerramento da sessão Hibernate, causando `LazyInitializationException`.

## Correção aplicada
- Adicionado ao `DiaMutiraoRepository` o método `findAllWithEventoByOrderByDataDesc()` com `@EntityGraph(attributePaths={"evento"})`.
- O formulário de solicitação usa essa consulta para receber os dias já com seus eventos carregados.
- Não altera tabelas nem exige migração Flyway.

## Aplicação
1. Faça backup do projeto.
2. Substitua os arquivos pelos deste pacote ou aplique as alterações indicadas.
3. Execute `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
4. Reinicie a aplicação e abra `/mutiroes/solicitacoes/nova`.

O ZIP foi validado quanto à integridade. O build Maven e a execução contra o PostgreSQL não foram realizados neste ambiente.
