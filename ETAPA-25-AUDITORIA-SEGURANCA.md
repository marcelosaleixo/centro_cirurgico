# Etapa 25 — Auditoria de segurança

## Incluído
- Migration Flyway V11: tabela `auditoria_seguranca` e índices por data/usuário.
- Registro de login bem-sucedido e falho, sem armazenar senhas ou detalhes internos de autenticação.
- Registro de alteração da própria senha.
- Registro de criação/edição de usuário e mudança de status de conta.
- Tela paginada de auditoria em `/admin/auditoria-seguranca`, exclusiva para `ADMIN_RH`.
- Retenção de histórico sem exclusão automática.

## Aplicação
Faça backup do banco. Na raiz do projeto:
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
O Flyway deverá aplicar V11 na inicialização. Acesse `/admin/auditoria-seguranca` após entrar com `ADMIN_RH`.

## Observações
- Não são registrados valores de senha.
- As falhas de login registram apenas o nome informado, a ação e um detalhe genérico.
- A compilação precisa ser validada localmente antes de produção.
