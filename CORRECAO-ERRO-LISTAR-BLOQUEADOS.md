# Correção — Painel de Segurança

## Causa
O `PainelSegurancaController` chamava `listarBloqueados()` e `desbloquearManualmente(...)`,
mas esses métodos não estavam presentes em `ControleTentativasLoginService`, causando erro
de compilação em tempo de execução.

## Correção
Foram adicionados:
- `listarBloqueados()`: lista contas ativas cujo bloqueio temporário ainda está vigente.
- `desbloquearManualmente(...)`: valida a justificativa (10 a 500 caracteres), libera a conta
  e grava a ação na auditoria de segurança.

## Validar no Windows / PowerShell
Na raiz do projeto, pare a aplicação e execute:

```powershell
.\mvnw.cmd clean test
```

Se passar, inicie novamente:

```powershell
.\mvnw.cmd spring-boot:run
```

Não foi adicionada migração de banco de dados nesta correção.
