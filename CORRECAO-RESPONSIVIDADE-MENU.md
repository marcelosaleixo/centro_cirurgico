# Correção da responsividade do menu lateral

## Causa
O menu tinha CSS para ocultar a sidebar em telas estreitas, mas não havia um controle externo visível para abri-la nem JavaScript ligado ao botão. Isso deixava a navegação inacessível em celular e telas estreitas.

## Correção
- Adicionado botão flutuante “Menu” fora da sidebar, visível abaixo de 900px.
- Adicionado JavaScript para abrir/fechar o menu, fechar ao clicar no fundo, pressionar Escape ou selecionar um link.
- Sidebar passa a usar altura dinâmica `100dvh`, rolagem vertical própria e largura limitada ao viewport.
- Conteúdo recebe margem superior no celular para não ficar escondido atrás do botão.
- Tabelas mantêm rolagem horizontal sem expandir a página.
- Atualizada a versão do CSS para evitar cache antigo.

## Aplicação
Substitua `src/main/resources/templates/fragments/layout.html` e `src/main/resources/static/css/app.css`. Depois execute `./mvnw clean test`, reinicie o servidor e atualize o navegador com Ctrl+F5.
