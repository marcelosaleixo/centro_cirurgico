# Correção da SegurancaConfig — Etapa 23

## Causa
O log mostra `Unresolved compilation problems` em `SegurancaConfig.java`, incluindo sintaxe inválida na chamada `authorizeHttpRequests`. O bean falha ao ser criado porque a classe compilada contém erros; as falhas de dependência de `usuarioSistemaController` e `gestaoUsuarioSistemaService` são consequências.

## Arquivo corrigido
Substitua `src/main/java/com/marceloaleixo/centrocirurgico/security/SegurancaConfig.java` pelo arquivo desta versão.

## Reconstrução no PowerShell
Na pasta que contém o `pom.xml`, pare a aplicação e execute:

```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Se estiver usando uma IDE, recarregue o projeto Maven antes de executar. O código mantém as regras: gestão de usuários e fechar/reabrir período exigem `ADMIN_RH`; salvar conferência exige `ADMIN_RH` ou `RH`; demais páginas exigem autenticação.

A compilação precisa ser validada no ambiente local.
