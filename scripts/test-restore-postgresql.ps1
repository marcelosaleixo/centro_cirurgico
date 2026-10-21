[CmdletBinding(SupportsShouldProcess=$true, ConfirmImpact='High')]
param(
    [Parameter(Mandatory=$true)][string]$BackupFile,
    [Parameter(Mandatory=$true)][string]$TargetDatabase,
    [string]$HostName = $env:PGHOST,
    [string]$Port = $(if ($env:PGPORT) { $env:PGPORT } else { "5432" }),
    [string]$Username = $env:PGUSER,
    [switch]$ConfirmRestore
)
$ErrorActionPreference = 'Stop'
if (-not $ConfirmRestore) { throw 'Operação interrompida por segurança. Informe -ConfirmRestore somente após confirmar que o banco de destino é descartável.' }
foreach ($tool in @('pg_restore','psql')) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) { throw "Comando '$tool' não encontrado. Instale o cliente PostgreSQL e inclua-o no PATH." }
}
if (-not (Test-Path $BackupFile)) { throw "Backup não encontrado: $BackupFile" }
if ([string]::IsNullOrWhiteSpace($HostName) -or [string]::IsNullOrWhiteSpace($Username)) { throw 'Defina PGHOST e PGUSER.' }
if ([string]::IsNullOrWhiteSpace($TargetDatabase) -or $TargetDatabase -match '[\s;]' ) { throw 'Nome do banco de destino inválido.' }
if ($env:PGDATABASE -and $TargetDatabase -eq $env:PGDATABASE) { throw 'Por segurança, o destino não pode ser igual ao banco de origem definido em PGDATABASE.' }
& pg_restore --list $BackupFile | Out-Null
if ($LASTEXITCODE -ne 0) { throw 'O arquivo não parece ser um backup custom válido.' }
$query = "SELECT 1 FROM pg_database WHERE datname = '$($TargetDatabase.Replace("'", "''"))';"
$exists = & psql --host=$HostName --port=$Port --username=$Username --dbname=postgres --tuples-only --no-align --command=$query
if ($LASTEXITCODE -ne 0) { throw 'Não foi possível consultar o servidor PostgreSQL.' }
if (($exists | Out-String).Trim() -eq '1') { throw "O banco de destino '$TargetDatabase' já existe. Escolha um banco descartável novo; o script não apaga bancos." }
if ($PSCmdlet.ShouldProcess($TargetDatabase, 'Criar banco descartável e restaurar backup')) {
    & psql --host=$HostName --port=$Port --username=$Username --dbname=postgres --command="CREATE DATABASE `"$TargetDatabase`";"
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar o banco de teste.' }
    & pg_restore --host=$HostName --port=$Port --username=$Username --dbname=$TargetDatabase --no-owner --no-privileges --exit-on-error $BackupFile
    if ($LASTEXITCODE -ne 0) { throw "A restauração falhou. O banco de teste '$TargetDatabase' foi mantido para investigação; remova-o manualmente após análise." }
    $check = & psql --host=$HostName --port=$Port --username=$Username --dbname=$TargetDatabase --tuples-only --no-align --command='SELECT current_database();'
    if ($LASTEXITCODE -ne 0 -or ($check | Out-String).Trim() -ne $TargetDatabase) { throw 'A verificação final da restauração falhou.' }
    $reportDir = '.\target\release-evidence'
    New-Item -ItemType Directory -Path $reportDir -Force | Out-Null
    @("Data: $(Get-Date -Format o)", "Backup: $([System.IO.Path]::GetFullPath($BackupFile))", "BancoTeste: $TargetDatabase", 'ResultadoRestauracao: APROVADA', 'Banco de teste mantido para inspeção; remoção manual necessária.') | Set-Content (Join-Path $reportDir 'teste-restauracao-postgresql.txt') -Encoding UTF8
    Write-Host "Restauração de teste concluída: $TargetDatabase" -ForegroundColor Green
}
