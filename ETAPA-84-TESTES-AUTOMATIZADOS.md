# Etapa 84 — Testes automatizados de bloqueio e rastreabilidade

## Objetivo
Adicionar testes unitários para as regras críticas de fechamento financeiro e registro de consumo cirúrgico, reduzindo o risco de regressões nas próximas etapas.

## Testes incluídos
- Rejeita validação sem identificador de cirurgia.
- Bloqueia o consumo quando o fechamento financeiro está `APROVADO`.
- Permite o consumo quando o fechamento está `ABERTO` ou ainda não existe.
- Valida campos obrigatórios do registro de consumo antes de consultar repositórios.

## Como executar no Windows PowerShell
Na pasta do projeto:

```powershell
.\mvnw.cmd -Dtest=BloqueioConsumoCirurgiaServiceTest,RastreabilidadeCirurgiaServiceTest test
```

Para executar a suíte completa:

```powershell
.\mvnw.cmd test
```

Se a distribuição Maven não estiver disponível, verifique a conexão com a internet e a configuração do Maven Wrapper. Os testes adicionados são unitários e não exigem uma instância PostgreSQL.

## Limites
Estes testes não substituem testes de integração com PostgreSQL nem validam todos os caminhos de movimentação genérica de estoque. Antes de produção, adicionar testes de integração para transações, rollback, concorrência, autorização por perfil e rotas alternativas de consumo.
