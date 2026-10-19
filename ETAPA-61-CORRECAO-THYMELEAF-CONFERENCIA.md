# Etapa 61 — Correção da tela de detalhe da solicitação

## Correção aplicada

Corrigida a expressão `th:classappend` na tela `templates/mutiroes/solicitacao-detalhe.html`. A expressão ternária tinha um parêntese de fechamento no lugar da chave `}`, impedindo o Thymeleaf de interpretar o template.

A expressão corrigida é:

```html
th:classappend="${i.quantidadeSeparada != null and i.quantidadeSeparada.compareTo(i.quantidadeSolicitada) == 0 ? 'text-bg-success' : 'text-bg-warning'}"
```

## Comportamento

- **Completo**: etiqueta verde quando a quantidade separada é igual à solicitada.
- **Pendente**: etiqueta amarela quando ainda não foi informada ou está abaixo da quantidade solicitada.
- Nenhuma alteração de banco de dados é necessária.
- Mantido o fluxo existente de aprovação, separação, impressão e confirmação de entrega.

## Teste recomendado

1. Execute `\.mvnw.cmd clean test`.
2. Inicie com `\.mvnw.cmd spring-boot:run`.
3. Abra uma solicitação em `/mutiroes/solicitacoes/{id}` e confirme que a página renderiza sem erro de parsing.
4. Confira os estados de itens completos e pendentes e teste **Imprimir / Salvar PDF**.

A validação realizada para este pacote é estrutural/ZIP; a compilação Maven e o teste com PostgreSQL precisam ser executados no ambiente do projeto.
