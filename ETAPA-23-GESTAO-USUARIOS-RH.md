# Etapa 23 — Gestão de usuários do sistema

## Funcionalidades
- Listagem de usuários do sistema.
- Cadastro de contas com senha inicial de no mínimo 12 caracteres, armazenada com BCrypt.
- Edição de nome de usuário e perfil (`ADMIN_RH`, `RH`, `CONSULTA`).
- Alteração opcional da senha.
- Ativação e desativação de contas.
- Proteção das rotas `/admin/usuarios/**` para o perfil `ADMIN_RH`.
- Impede desativar o último administrador ativo.

## Acesso
Entre com uma conta `ADMIN_RH` e acesse `/admin/usuarios`.

## Execução
No PowerShell, na pasta que contém o `pom.xml`:
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

## Observações
- Não foi criada migration nova: a etapa usa a tabela `usuarios_sistema` da migration V10.
- Não há exclusão física de contas; use a desativação para preservar referências e histórico.
- Faça backup do banco antes de atualizar.
- A compilação precisa ser confirmada no ambiente local.
