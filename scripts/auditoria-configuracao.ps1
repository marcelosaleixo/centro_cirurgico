param(
    [switch]$Producao
)
$ErrorActionPreference = 'Stop'
if (-not (Test-Path '.\pom.xml')) { throw 'Execute na raiz do projeto (pom.xml).' }
$propsPath = '.\src\main\resources\application.properties'
if (-not (Test-Path $propsPath)) { throw "Arquivo não encontrado: $propsPath" }
$props = Get-Content $propsPath -Raw
$issues = [System.Collections.Generic.List[string]]::new()
$warnings = [System.Collections.Generic.List[string]]::new()

if ($Producao) {
    if ([string]::IsNullOrWhiteSpace($env:DB_URL)) { $issues.Add('Defina DB_URL para o PostgreSQL de produção.') }
    if ([string]::IsNullOrWhiteSpace($env:DB_USERNAME)) { $issues.Add('Defina DB_USERNAME.') }
    if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) { $issues.Add('Defina DB_PASSWORD em variável secreta do ambiente.') }
    if ([string]::IsNullOrWhiteSpace($env:RH_ADMIN_PASSWORD)) { $warnings.Add('RH_ADMIN_PASSWORD não está definida; confirme o procedimento seguro de criação do administrador inicial.') }
    if ($props -match 'ddl-auto\s*=\s*update') { $issues.Add('Não use spring.jpa.hibernate.ddl-auto=update em produção.') }
    if ($props -match 'show-sql\s*=\s*true') { $warnings.Add('SQL logging estático parece habilitado; revise exposição de dados nos logs.') }
    if ($props -match 'jdbc:postgresql://localhost') { $warnings.Add('Há URL local de fallback; confirme que DB_URL é definida no ambiente de produção.') }
    if ($props -match 'password\s*=\s*postgres') { $warnings.Add('Existe senha padrão postgres no arquivo de configuração; confirme que ela só funciona como fallback local e não em produção.') }
} else {
    Write-Host 'Modo local: verificações de produção não bloqueantes.' -ForegroundColor Yellow
    if ($props -match 'password\s*=\s*postgres') { $warnings.Add('Senha fallback postgres detectada; não a reutilize em produção.') }
}
if (-not (Test-Path '.\.gitignore')) { $warnings.Add('.gitignore não encontrado; confirme que .env e arquivos de segredos não são versionados.') }
elseif ((Get-Content '.\.gitignore' -Raw) -notmatch '(?m)^\.env/?$|(?m)^\.env\*') { $warnings.Add('.gitignore não parece ignorar .env; adicione .env e mantenha .env.example sem segredos.') }
if (-not (Test-Path '.\src\main\resources\db\migration')) { $issues.Add('Diretório de migrações Flyway não encontrado.') }

Write-Host '=== Auditoria de configuração Centro Cirúrgico ===' -ForegroundColor Cyan
foreach ($w in $warnings) { Write-Host "AVISO: $w" -ForegroundColor Yellow }
foreach ($i in $issues) { Write-Host "ERRO: $i" -ForegroundColor Red }
if ($issues.Count -gt 0) { throw "Auditoria reprovada: $($issues.Count) problema(s) bloqueante(s)." }
Write-Host 'Nenhum bloqueio estático encontrado. Isso não substitui teste de conexão, segurança ou homologação.' -ForegroundColor Green
if ($warnings.Count -gt 0) { Write-Host "Avisos: $($warnings.Count). Revise antes de publicar." -ForegroundColor Yellow }
