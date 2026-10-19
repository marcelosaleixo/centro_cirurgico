\
# Backup PostgreSQL no Windows PowerShell
# Requer pg_dump no PATH (instalação do PostgreSQL).
$ErrorActionPreference = "Stop"

if (-not $env:DB_HOST) { throw "Defina DB_HOST" }
if (-not $env:DB_NAME) { throw "Defina DB_NAME" }
if (-not $env:DB_USERNAME) { throw "Defina DB_USERNAME" }
if (-not $env:DB_PASSWORD) { throw "Defina DB_PASSWORD" }
if (-not $env:BACKUP_DIR) { $env:BACKUP_DIR = ".\backups" }
if (-not $env:DB_PORT) { $env:DB_PORT = "5432" }

New-Item -ItemType Directory -Force -Path $env:BACKUP_DIR | Out-Null
$timestamp = (Get-Date).ToUniversalTime().ToString("yyyyMMddTHHmmssZ")
$file = Join-Path $env:BACKUP_DIR "$($env:DB_NAME)_$timestamp.dump"
$env:PGPASSWORD = $env:DB_PASSWORD

try {
    & pg_dump --host=$env:DB_HOST --port=$env:DB_PORT --username=$env:DB_USERNAME `
        --dbname=$env:DB_NAME --format=custom --no-owner --no-privileges --file=$file
    if ($LASTEXITCODE -ne 0) { throw "pg_dump falhou com código $LASTEXITCODE" }
    $hash = Get-FileHash -Algorithm SHA256 -Path $file
    "$($hash.Hash)  $([IO.Path]::GetFileName($file))" |
        Set-Content -Encoding ascii "$file.sha256"
    Write-Host "Backup concluído: $file"
}
finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
