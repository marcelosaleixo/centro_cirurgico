# Etapa 31 — Auditoria de operações

## O que esta etapa adiciona
- Migração Flyway V13 cria `auditoria_operacoes` com índice por data e usuário.
- Interceptor registra requisições POST, PUT, PATCH e DELETE com usuário autenticado, método, caminho, código HTTP e resultado.
- Não registra corpo, parâmetros de formulário, query string nem senhas.
- Página paginada `/admin/auditoria-operacoes`, restrita a `ADMIN_RH`.
- A auditoria é persistida em transação independente; falha de auditoria não mascara a resposta original.

## Aplicação
1. Faça backup do projeto e do banco.
2. Extraia esta versão em pasta separada.
3. Configure as variáveis de ambiente já usadas no projeto e execute `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
4. Inicie a aplicação. Flyway aplicará a migração V13 automaticamente.
5. Com perfil `ADMIN_RH`, acesse `/admin/auditoria-operacoes` e faça uma operação de cadastro/edição para verificar o registro.

## Limitações importantes
- Registra a rota e o resultado HTTP, não um diff dos valores antes/depois.
- Rotas de login/logout e troca de senha são excluídas por serem tratadas por auditorias próprias.
- Operações via tarefas fora do ciclo HTTP (por exemplo, jobs internos) não são capturadas por este interceptor.
- Para produção hospitalar, mantenha controle de acesso, backups e monitoramento dos erros de persistência da auditoria.
