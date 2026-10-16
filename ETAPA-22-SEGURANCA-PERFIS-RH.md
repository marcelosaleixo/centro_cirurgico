# Etapa 22 — Segurança, perfis e responsável pela auditoria

## Alterações
- Login via Spring Security e formulário Thymeleaf.
- Usuários persistidos na tabela `usuarios_sistema` (migration Flyway V10).
- Perfis `ADMIN_RH`, `RH` e `CONSULTA`.
- Somente `ADMIN_RH` pode fechar/reabrir período; `ADMIN_RH` e `RH` podem salvar conferências.
- Auditoria passa a registrar o usuário autenticado, em vez de sempre usar `sistema`.
- Usuário administrador inicial opcional via variáveis `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD`; senha precisa ter ao menos 12 caracteres.

## Configuração inicial
Defina as variáveis de ambiente antes de iniciar a aplicação. Exemplo no PowerShell (apenas para a sessão atual):

```powershell
$env:RH_ADMIN_USERNAME = "admin.rh"
$env:RH_ADMIN_PASSWORD = "TROQUE-POR-UMA-SENHA-FORTE-DE-12-OU-MAIS-CARACTERES"
.\mvnw.cmd spring-boot:run
```

O usuário só é criado se não existir outro com o mesmo nome. Não use a senha de exemplo em produção. Se as variáveis não estiverem definidas, nenhum administrador inicial será criado automaticamente.

## Perfis
O perfil deve ser exatamente `ADMIN_RH`, `RH` ou `CONSULTA`. A criação/atribuição de perfis para outros usuários ainda deve ser feita por processo administrativo controlado; esta etapa não inclui tela de gestão de usuários.

## Validação
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

O Spring Security protege as rotas de escrita indicadas e solicita autenticação para as demais páginas. Como esta aplicação agora exige login, configure o usuário administrador antes de acessar o sistema. Confirme a compilação e teste login, logout, permissões dos perfis e migrations em uma cópia do banco antes de publicar.
