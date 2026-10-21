# Correção Etapa 93 — Menu por módulos

## Causa
O JavaScript que controlava os módulos estava fora do fragmento `navbar` do Thymeleaf. Ao usar `th:replace` para incluir apenas `fragments/layout :: navbar`, os botões eram renderizados mas o script ficava de fora; por isso clicar nos títulos não abria os itens.

## Correção
O listener delegado dos módulos foi colocado dentro do fragmento `navbar`, diretamente associado a `#sidebarNavigation`. Cada botão alterna `aria-expanded`, a classe visual e a propriedade `hidden` do painel apontado por `aria-controls`.

## Validação
Inspecionar o template e a integridade do ZIP; executar `./mvnw clean compile` e testar no navegador no ambiente do projeto.
