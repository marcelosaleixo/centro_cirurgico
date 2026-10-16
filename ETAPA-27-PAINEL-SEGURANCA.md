# Etapa 27 — Painel administrativo de segurança

## Incluído
- Lista de contas ativas cujo bloqueio temporário ainda está vigente.
- Exibe usuário, contador de tentativas e horário de desbloqueio.
- Desbloqueio manual com justificativa obrigatória (10 a 500 caracteres).
- Zera o contador de tentativas ao desbloquear.
- Registra `DESBLOQUEIO_MANUAL` na auditoria, com administrador, conta alvo e justificativa.
- Acesso restrito a `ADMIN_RH`.
- Não cria migration nova; utiliza as colunas da migration V12.

## Executar
Faça backup do banco. Na pasta que contém `pom.xml`:
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Após iniciar, entre como `ADMIN_RH` e acesse `/admin/seguranca`.

A compilação precisa ser confirmada no ambiente local.
