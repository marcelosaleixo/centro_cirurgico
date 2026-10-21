$ErrorActionPreference = 'Stop'

if (-not (Test-Path '.\pom.xml')) {
    throw 'Execute este script na raiz do projeto, onde está o pom.xml.'
}

$maven = if (Test-Path '.\mvnw.cmd') { '.\mvnw.cmd' } else { 'mvn' }

Write-Host '=== Etapa 97: compilação limpa ===' -ForegroundColor Cyan
& $maven clean compile
if ($LASTEXITCODE -ne 0) { throw 'Falha em clean compile. Corrija os erros antes de continuar.' }

Write-Host '=== Etapa 97: testes ===' -ForegroundColor Cyan
& $maven test
if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes. Consulte target/surefire-reports e target/failsafe-reports.' }

Write-Host '=== Etapa 97: empacotamento ===' -ForegroundColor Cyan
& $maven package
if ($LASTEXITCODE -ne 0) { throw 'Falha no empacotamento.' }

Write-Host 'Validação Maven concluída. Ainda execute os testes funcionais e de restauração descritos em ETAPA-97-VALIDACAO-FINAL-PRE-PRODUCAO.md.' -ForegroundColor Green
