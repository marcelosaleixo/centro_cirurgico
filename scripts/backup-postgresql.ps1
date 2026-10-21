[CmdletBinding()]
param(
    [string]$OutputDirectory = ".\backups",
    [string]$HostName = $env:PGHOST,
    [string]$Port = $(if ($env:PGPORT) { $env:PGPORT } else { "5432" }),
    [string]$Database = $env:PGDATABASE,
    [string]$Username = $env:PGUSER
)
$ErrorActionPreference = 'Stop'
foreach ($tool in @('pg_dump','pg_restore')) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) { throw "Comando '$tool' não encontrado. Instale o cliente PostgreSQL e inclua-o no PATH." }
}
if ([string]::IsNullOrWhiteSpace($HostName) -or [string]::IsNullOrWhiteSpace($Database) -or [string]::IsNullOrWhiteSpace($Username)) {
    throw 'Defina PGHOST, PGDATABASE e PGUSER, ou informe os parâmetros correspondentes.'
}
if ([string]::IsNullOrWhiteSpace($env:PGPASSWORD)) { Write-Warning 'PGPASSWORD não está definido. O cliente poderá solicitar senha interativamente.' }
$dir = [System.IO.Path]::GetFullPath($OutputDirectory)
New-Item -ItemType Directory -Path $dir -Force | Out-Null
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$file = Join-Path $dir ("centrocirurgico-{0}-{1}.dump" -f $Database,$stamp)
$env:PGCONNECT_TIMEOUT = '10'
try {
    & pg_dump --host=$HostName --port=$Port --username=$Username --dbname=$Database --format=custom --no-owner --no-privileges --file=$file
    if ($LASTEXITCODE -ne 0) { throw "pg_dump falhou com código $LASTEXITCODE." }
    if (-not (Test-Path $file) -or (Get-Item $file).Length -le 0) { throw 'O arquivo de backup não foi criado ou está vazio.' }
    & pg_restore --list $file | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'O arquivo de backup não passou na verificação pg_restore --list.' }
    $hash = (Get-FileHash -Algorithm SHA256 $file).Hash
    $report = Join-Path $dir ("centrocirurgico-{0}-{1}.sha256.txt" -f $Database,$stamp)
    @("Arquivo: $([System.IO.Path]::GetFileName($file))", "Banco: $Database", "Data: $(Get-Date -Format o)", "TamanhoBytes: $((Get-Item $file).Length)", "SHA256: $hash", 'VerificacaoEstrutural: APROVADA', 'RestauracaoReal: NAO EXECUTADA') | Set-Content -Path $report -Encoding UTF8
    Write-Host "Backup criado e verificado: $file" -ForegroundColor Green
    Write-Host "Evidência SHA-256: $report"
    Write-Warning 'A listagem do arquivo não comprova restauração. Faça um ensaio de restauração em banco descartável separado.'
} catch {
    if (Test-Path $file) { Remove-Item $file -Force -ErrorAction SilentlyContinue }
    throw
} finally {
    Remove-Item Env:PGCONNECT_TIMEOUT -ErrorAction SilentlyContinue
}
