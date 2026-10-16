# Etapa 38 — Layout responsivo e paginação reutilizável

## Alterações
- Menu principal convertido em sidebar fixa em telas grandes e drawer em telas menores.
- Botão de menu acessível por teclado (Escape fecha o menu) e fundo de sobreposição no mobile.
- Layout de tabelas com paginação reutilizável em JavaScript, ativada automaticamente quando a tabela contém mais de 10 linhas.
- Criado fragmento Thymeleaf `fragments/paginacao.html` com estrutura de paginação e script reutilizável.
- Não foi adicionada migração de banco de dados.

## Importante sobre paginação
A paginação automática atual é no navegador: limita a visualização a 10 linhas por página, mas as linhas já precisam ter sido carregadas pela listagem. Para tabelas grandes, recomenda-se converter cada consulta para paginação no servidor usando `Page<T>` e `Pageable`, mantendo o fragmento como controle de navegação.

## Teste
1. Execute `./mvnw clean test` (Linux/macOS) ou `./mvnw.cmd clean test` (Windows).
2. Verifique o menu em desktop e em viewport móvel.
3. Em uma listagem com mais de 10 registros, use Anterior/Próxima.
4. Confirme que formulários, login, calendário e ações existentes continuam funcionando.
