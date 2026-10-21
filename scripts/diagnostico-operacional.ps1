param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$Relatorio = '.\target\release-evidence\diagnostico-operacional.txt',
    [int]$TimeoutMs = 3000
)
$ErrorActionPreference = 'Stop'
$results = [System.Collections.Generic.List[string]]::new()
$failed = $false
function Add-Result([string]$Name,[bool]$Ok,[string]$Detail) {
    $status = if ($Ok) { 'PASS' } else { 'FAIL' }
    $line = "$status | $Name | $Detail"
    $script:results.Add($line)
    Write-Host $line -ForegroundColor $(if($Ok){'Green'}else{'Red'})
    if (-not $Ok) { $script:failed = $true }
}
$base = $BaseUrl.TrimEnd('/')
$results.Add("Diagnostico operacional | $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz') | BaseUrl=$base")
# Verifica se o endpoint base responde; não interpreta redirecionamento para login como indisponibilidade.
try {
    $r = Invoke-WebRequest -Uri $base -Method Get -MaximumRedirection 0 -TimeoutSec 10 -UseBasicParsing -ErrorAction Stop
    $code = [int]$r.StatusCode
} catch {
    $code = 0
    if ($_.Exception.Response) { try { $code = [int]$_.Exception.Response.StatusCode.value__ } catch {} }
}
Add-Result 'Aplicacao HTTP' ($code -in @(200,301,302,303,307,308,401,403)) "HTTP $code; URL=$base"
# Analisa DB_URL sem imprimir usuário, senha, query string ou URL completa.
$dbUrl = $env:DB_URL
if ([string]::IsNullOrWhiteSpace($dbUrl)) {
    Add-Result 'DB_URL configurada' $false 'Variavel DB_URL ausente; configure-a no ambiente antes do teste de banco.'
} else {
    try {
        $uriText = $dbUrl -replace '^jdbc:', ''
        $uri = [System.Uri]$uriText
        $hostName = $uri.Host
        $port = $uri.Port
        if ($port -le 0) { $port = 5432 }
        Add-Result 'DB_URL configurada' $true "Host=$hostName; Porta=$port (credenciais ocultadas)"
        $client = [System.Net.Sockets.TcpClient]::new()
        try {
            $task = $client.ConnectAsync($hostName, $port)
            if (-not $task.Wait($TimeoutMs)) { throw "Timeout TCP apos ${TimeoutMs}ms" }
            if (-not $client.Connected) { throw 'Conexao TCP nao estabelecida' }
            Add-Result 'PostgreSQL TCP' $true "Conexao TCP aberta em ${hostName}:${port}; autenticacao SQL nao foi testada"
        } finally { $client.Dispose() }
    } catch {
        Add-Result 'PostgreSQL TCP' $false "Falha ao interpretar DB_URL ou conectar: $($_.Exception.Message)"
    }
}
$results.Add("RESULTADO=$(if($failed){'FALHOU'}else{'PASSOU — diagnostico basico; autenticacao e consultas SQL nao verificadas'})")
$dir = Split-Path -Parent $Relatorio
if ($dir) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
Set-Content -Path $Relatorio -Value $results -Encoding UTF8
Write-Host "Relatorio salvo em $Relatorio"
if ($failed) { exit 1 }
