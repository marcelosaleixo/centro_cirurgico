# Centro Cirúrgico — Etapa 29: endurecimento de segurança HTTP

Base: correção da listagem/desbloqueio do painel de segurança (Etapa 27), com as configurações de sessão da Etapa 28.

## O que foi adicionado
- CSRF explicitamente mantido habilitado. Não desabilitar CSRF globalmente.
- Cabeçalhos `X-Content-Type-Options`, `X-Frame-Options: DENY` e política de referência.
- Content Security Policy inicial para restringir recursos a mesma origem, permitindo estilos/scripts inline por compatibilidade com telas existentes. Recomenda-se remover inline scripts/styles progressivamente para endurecer essa política.
- Cookies de sessão `HttpOnly` e `SameSite=Strict`. A opção `Secure` é configurável.

## Configuração
Em ambiente local HTTP, mantenha `SESSION_COOKIE_SECURE=false`. Em produção, após configurar HTTPS, defina `SESSION_COOKIE_SECURE=true`. Não habilite `Secure` em HTTP puro, pois o navegador não enviará o cookie.

## Validação
Na raiz do projeto execute:

```powershell
.\mvnw.cmd clean test
```

Se passar:

```powershell
.\mvnw.cmd spring-boot:run
```

Teste login, logout, alteração de senha, formulários de cadastro/edição, fechamento e reabertura do RH, auditoria e painel de segurança. Verifique no navegador (DevTools > Network > Response Headers) os cabeçalhos de segurança. Se alguma tela deixar de carregar recursos por causa da CSP, identifique o recurso bloqueado no console e ajuste a política conscientemente; não remova os demais cabeçalhos.

## Importante
Este pacote foi gerado a partir do código-fonte disponível, mas a compilação/testes não foram executados neste ambiente. Faça backup antes de substituir seu projeto.
