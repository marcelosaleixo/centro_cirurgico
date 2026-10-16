# Correção do erro de placeholder circular

## Causa
O `application.properties` usava `RH_ADMIN_USERNAME=${RH_ADMIN_USERNAME:admin.rh}`.
Como a propriedade tinha o mesmo nome da variável usada no placeholder, o Spring resolvia a referência de forma circular.

## Correção
As configurações internas agora usam nomes próprios:
- `app.bootstrap.admin.username=${RH_ADMIN_USERNAME:admin.rh}`
- `app.bootstrap.admin.password=${RH_ADMIN_PASSWORD:}`

A classe `UsuarioSistemaInicializador` lê `app.bootstrap.admin.username` e `app.bootstrap.admin.password`. As variáveis de ambiente continuam sendo `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD`.

## Configuração
Defina no PowerShell antes de iniciar a aplicação, usando uma senha exclusiva com pelo menos 12 caracteres:

```powershell
$env:RH_ADMIN_USERNAME="admin.rh"
$env:RH_ADMIN_PASSWORD="SUA_SENHA_FORTE_COM_12_OU_MAIS_CARACTERES"
```

Depois execute:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Se a conta já existir, o inicializador mantém a senha atual. Se não existir, cria a conta com perfil `ADMIN_RH` e senha codificada pelo `PasswordEncoder`.

Não foi possível executar os testes Maven neste ambiente; valide a compilação localmente.
