# Correção visual — Dashboard gerencial de suprimentos

## O que foi alterado
- O dashboard agora utiliza o fragmento global `fragments/layout :: head`, que carrega o CSS local `/css/app.css` e o layout/menu lateral compartilhado.
- Removida a dependência do Bootstrap CDN para a estrutura visual desta página.
- Aplicado layout responsivo próprio com cartões de indicadores, status, indicadores de conferência e tabela com rolagem horizontal em telas pequenas.
- Mantidas as expressões Thymeleaf e as rotas de solicitações e relatórios existentes.

## Aplicação
Substitua `src/main/resources/templates/mutiroes/dashboard-suprimentos.html` pelo arquivo deste pacote. Para aplicar o projeto completo deste ZIP, faça backup da pasta atual antes de substituir.

Na raiz do projeto, execute:

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

Depois faça uma recarga forçada no navegador (`Ctrl+F5`). O projeto usa o CSS local `src/main/resources/static/css/app.css`; a tela não depende de carregar o Bootstrap pela internet.
