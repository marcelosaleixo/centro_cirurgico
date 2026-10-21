# Etapa 100 — Smoke test pós-inicialização

Adiciona `scripts/smoke-test-producao.ps1` para testar a resposta HTTP básica das rotas principais e registrar evidências em `target/release-evidence/smoke-test.txt`.

Execute com a aplicação já iniciada:

```powershell
.\scripts\smoke-test-producao.ps1
```

Ou informe a URL base:

```powershell
.\scripts\smoke-test-producao.ps1 -BaseUrl 'https://seu-dominio'
```

O script é somente leitura. HTTP 200 não garante que o sistema esteja pronto para produção; use o checklist `ETAPA-100-SMOKE-TEST-EVIDENCIAS.md` e valide os fluxos com PostgreSQL e usuários reais de homologação.
