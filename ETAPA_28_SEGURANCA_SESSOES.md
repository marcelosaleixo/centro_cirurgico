# Etapa 28 — Segurança de sessões

## O que foi implementado
- Tempo máximo de inatividade configurável por `SESSION_TIMEOUT` (padrão: `15m`).
- Redirecionamento para o login quando a sessão expira ou é inválida.
- Limite de uma sessão simultânea por usuário; um novo login encerra a sessão anterior.
- Proteção contra session fixation migrando a sessão após autenticação.
- Logout invalida a sessão, limpa a autenticação e remove o cookie `JSESSIONID`.
- Mensagens no login para sessão expirada e login em outro local.
- Botão de saída via formulário POST (compatível com CSRF do Spring Security).

## Configuração
No `application.properties`:
`server.servlet.session.timeout=${SESSION_TIMEOUT:15m}`

Para alterar no ambiente, configure, por exemplo:
`SESSION_TIMEOUT=20m`

O contêiner/servidor pode ter um limite máximo de sessão próprio; em produção, confira também proxy e configuração do servidor.

## Como testar
1. Execute a aplicação e faça login.
2. Aguarde o período de inatividade configurado; ao fazer nova requisição, deverá voltar ao login com aviso de sessão expirada.
3. Faça login no mesmo usuário em uma segunda janela/navegador. A sessão anterior deverá ser invalidada.
4. Clique em **Sair** e confirme que o acesso às páginas protegidas exige novo login.
5. Teste com usuários `ADMIN_RH` e `RH` para garantir que as permissões anteriores continuam funcionando.

## Observação
Este pacote foi montado sobre a Etapa 27. Não foi possível confirmar a compilação ou executar testes automatizados neste ambiente; valide com `.\mvnw.cmd clean test` e depois `.\mvnw.cmd spring-boot:run` no seu ambiente. Se houver erro, envie o log completo.
