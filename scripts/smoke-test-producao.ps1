param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$Relatorio = '.\target\release-evidence\smoke-test.txt',
    [switch]$VerificarActuator
)
$ErrorActionPreference = 'Stop'
$base = $BaseUrl.TrimEnd('/')
$out = Split-Path -Parent $Relatorio
if ($out) { New-Item -ItemType Directory -Force -Path $out | Out-Null }
$results = [System.Collections.Generic.List[string]]::new()
$failed = $false
function Test-Url([string]$Path, [int[]]$Expected, [string]$Label) {
    $uri = "$base$Path"
    try {
        $response = Invoke-WebRequest -Uri $uri -Method Get -MaximumRedirection 0 -TimeoutSec 15 -UseBasicParsing -ErrorAction Stop
        $status = [int]$response.StatusCode
    } catch {
        if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode.value__ }
        else { $status = 0 }
    }
    $ok = $Expected -contains $status
    $line = "$(if($ok){'PASS'}else{'FAIL'}) | $Label | $uri | HTTP $status | esperado: $($Expected -join ',')"
    $results.Add($line)
    Write-Host $line -ForegroundColor $(if($ok){'Green'}else{'Red'})
    if (-not $ok) { $script:failed = $true }
}
$results.Add("Smoke test Centro Cirúrgico | $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz') | BaseUrl=$base")
# O login pode responder 200; páginas protegidas podem redirecionar para login (302/303) ou negar acesso (401/403).
Test-Url '/' @(200,301,302,303,401,403) 'Rota inicial responde'
Test-Url '/login' @(200,301,302,303) 'Tela de login'
Test-Url '/mutiroes/dashboard-suprimentos' @(200,301,302,303,401,403) 'Dashboard de suprimentos responde sem erro 5xx'
Test-Url '/dashboard' @(200,301,302,303,401,403) 'Dashboard geral responde sem erro 5xx'
if ($VerificarActuator) { Test-Url '/actuator/health' @(200) 'Actuator health' }
$results.Add("RESULTADO=$(if($failed){'FALHOU'}else{'PASSOU — smoke test básico; homologação funcional ainda necessária'})")
Set-Content -Path $Relatorio -Value $results -Encoding UTF8
Write-Host "Relatório salvo em $Relatorio"
if ($failed) { exit 1 }
