CORREÇÃO — DATAS NO FORMULÁRIO DE EDIÇÃO DO MUTIRÃO

Problema corrigido
Os campos Data inicial e Data final agora recebem explicitamente valores no formato ISO yyyy-MM-dd, exigido por inputs HTML do tipo date. Isso evita que as datas apareçam vazias na tela de edição mesmo quando existem no banco.

Arquivo alterado
- src/main/resources/templates/mutiroes/formulario.html

Instalação
1. Faça backup do código atual.
2. Extraia este ZIP e atualize o repositório.
3. Reinicie/reimplante a aplicação. Não há nova migração de banco.
4. Abra /mutiroes, edite um evento existente e confira as datas inicial e final.

Validação
O ZIP de origem e o ZIP corrigido foram empacotados; a integridade estrutural pode ser conferida com unzip -t. Não foi executado build Maven nem teste integrado com PostgreSQL nesta sessão.
