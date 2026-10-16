# Correção — nenhum usuário cadastrado

## Causa
A migration V10 cria a tabela `usuarios_sistema`, mas não insere um usuário. O inicializador cria o administrador apenas quando `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD` estão definidos no ambiente do processo Java; caso contrário, encerra sem criar a conta. O banco mostrado está sem registros.

## Configure no PowerShell (mesma janela em que iniciará a aplicação)
```powershell
$env:RH_ADMIN_USERNAME = "admin.rh"
$env:RH_ADMIN_PASSWORD = "Escolha-Uma-Senha-Forte-12+"
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Substitua a senha de exemplo por uma senha forte, exclusiva e de pelo menos 12 caracteres. Não publique nem compartilhe a senha.

## Verifique no PostgreSQL
```sql
SELECT id, username, perfil, ativo FROM usuarios_sistema;
```
Deve aparecer a conta `admin.rh` com perfil `ADMIN_RH` e `ativo = true`.

## Importante
- Variáveis definidas com `$env:` valem para aquela janela PowerShell; se iniciar a aplicação pela IDE, configure-as também em Run/Debug Configuration > Environment variables.
- Se o usuário já existir, o inicializador não troca sua senha; se estiver inativo ou com perfil diferente, reativa/promove para `ADMIN_RH` mantendo a senha existente.
- Não foi criada migration nova nem senha padrão fixa.
- A compilação precisa ser confirmada no ambiente local.
