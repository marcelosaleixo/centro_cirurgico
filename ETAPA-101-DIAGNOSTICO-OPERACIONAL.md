# Etapa 101 — Diagnóstico operacional da aplicação e do PostgreSQL

## Objetivo

Adicionar uma verificação rápida para separar falhas de disponibilidade HTTP, ausência de `DB_URL` e falhas de conectividade TCP com o PostgreSQL. O diagnóstico não imprime credenciais e grava evidências locais.

## Execução (Windows PowerShell)

Com a aplicação iniciada:

```powershell
.\scripts\diagnostico-operacional.ps1
```

Para outro endereço da aplicação:

```powershell
.\scripts\diagnostico-operacional.ps1 -BaseUrl "https://seu-dominio"
```

Para aumentar o limite de espera TCP (milissegundos):

```powershell
.\scripts\diagnostico-operacional.ps1 -TimeoutMs 5000
```

O relatório é salvo em `target/release-evidence/diagnostico-operacional.txt`.

## Interpretação

- `Aplicacao HTTP`: considera respostas normais e redirecionamentos/autorização como sinal de que o servidor HTTP respondeu; não valida o conteúdo funcional da página.
- `DB_URL configurada`: verifica se a variável existe e extrai somente host/porta, sem expor usuário, senha ou query string.
- `PostgreSQL TCP`: verifica somente a abertura de conexão TCP. **Não comprova autenticação, existência do banco, permissões, migrações Flyway nem consultas SQL.**
- Qualquer `FAIL` gera código de saída diferente de zero.

## Antes de produção

1. Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` como variáveis seguras no ambiente de implantação.
2. Execute os testes Maven e valide Flyway em banco de homologação.
3. Faça login e teste permissões com contas de perfis distintos.
4. Valide backup e restauração, além dos fluxos de escalas, estoque, cirurgias e fechamento financeiro.

## Limites desta etapa

O script é uma ferramenta de diagnóstico básico. Não altera o banco, não reinicia serviços e não deve ser interpretado como certificação de produção. O teste ainda precisa ser executado no ambiente real.
