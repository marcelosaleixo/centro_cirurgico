param(
    [switch]$AuditoriaProducao
)
$ErrorActionPreference = 'Stop'
if (-not (Test-Path '.\pom.xml')) { throw 'Execute este script na raiz do projeto (pom.xml).' }
$maven = if (Test-Path '.\mvnw.cmd') { '.\mvnw.cmd' } else { 'mvn' }
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$outDir = '.\target\release-evidence'
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$report = Join-Path $outDir "ensaio-liberacao-$stamp.txt"
$script:failed = $false
function Run-Step([string]$Title, [scriptblock]$Command) {
    Add-Content -Path $report -Value "`r`n=== $Title ==="
    Write-Host "=== $Title ===" -ForegroundColor Cyan
    try {
        & $Command 2>&1 | Tee-Object -FilePath $report -Append | Out-Host
        $code = $LASTEXITCODE
        if ($null -eq $code) { $code = 0 }
        Add-Content -Path $report -Value "EXIT_CODE=$code"
        if ($code -ne 0) { $script:failed = $true; Write-Host "Falhou: $Title" -ForegroundColor Red }
    } catch {
        $script:failed = $true
        Add-Content -Path $report -Value "EXCEPTION=$($_.Exception.Message)"
        Write-Host "Falhou: $Title — $($_.Exception.Message)" -ForegroundColor Red
    }
}
@("Centro Cirúrgico — Ensaio de liberação", "Data local: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')", "Pasta: $(Get-Location)", "Java:") | Set-Content -Path $report
try { (& java -version 2>&1 | Out-String).Trim() | Add-Content -Path $report } catch { Add-Content -Path $report -Value 'Java não encontrado no PATH.' }
if ($AuditoriaProducao) {
    Run-Step 'Auditoria estática de produção' { & powershell -NoProfile -ExecutionPolicy Bypass -File '.\scripts\auditoria-configuracao.ps1' -Producao }
}
Run-Step 'Compilação limpa' { & $maven clean compile }
if (-not $script:failed) { Run-Step 'Testes automatizados' { & $maven test } }
if (-not $script:failed) { Run-Step 'Empacotamento' { & $maven package } }
Add-Content -Path $report -Value "`r`nRESULTADO=$(if ($script:failed) { 'REPROVADO' } else { 'COMANDOS CONCLUIDOS; HOMOLOGACAO MANUAL AINDA OBRIGATORIA' })"
Write-Host "Relatório: $report" -ForegroundColor Green
if ($script:failed) { throw 'Ensaio reprovado. Consulte o relatório e corrija as falhas antes de liberar.' }
Write-Host 'Comandos concluídos. Isso não substitui homologação funcional, validação de PostgreSQL ou teste de restauração.' -ForegroundColor Yellow
