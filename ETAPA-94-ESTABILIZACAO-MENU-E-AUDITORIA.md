# Etapa 94 — Estabilização do menu modular

## Causa raiz

O fragmento `templates/fragments/layout.html` registrava dois manipuladores de clique para cada botão de módulo:

1. Um manipulador delegado em `#sidebarNavigation`.
2. Um segundo manipulador individual criado por `initSidebarModules()`.

Ambos invertiam `aria-expanded` e a propriedade `hidden`. Como os dois eram executados no mesmo clique, o estado era invertido duas vezes e o painel terminava recolhido, parecendo que o menu não respondia.

## Correção

- Mantido apenas o manipulador delegado dentro do fragmento do menu.
- Removido o registro duplicado de `initSidebarModules()`.
- Preservada a lógica de abertura/fechamento do menu lateral responsivo.
- Preservados os caminhos de navegação existentes e os atributos de acessibilidade.

## Validação recomendada

1. Executar `./mvnw clean compile` (Windows: `mvnw.cmd clean compile`).
2. Reiniciar a aplicação.
3. Atualizar o navegador com `Ctrl + F5`.
4. Clicar uma vez em cada cabeçalho de módulo: os links devem aparecer.
5. Clicar novamente: os links devem ser ocultados.
6. Repetir em largura de desktop e celular e confirmar que o botão de menu lateral continua abrindo e fechando o painel.

A correção de JavaScript foi inspecionada no código-fonte. A compilação e o teste em navegador/PostgreSQL devem ser executados no ambiente do projeto.
