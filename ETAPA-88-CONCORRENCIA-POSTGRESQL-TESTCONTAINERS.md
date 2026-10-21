# Etapa 88 — Teste de concorrência real com PostgreSQL e Testcontainers

## Objetivo

Adicionar um teste automatizado que inicia um PostgreSQL temporário via Testcontainers e executa duas baixas concorrentes contra o mesmo saldo. O cenário começa com 5 unidades; ambas tentam retirar 4. O resultado esperado é exatamente uma baixa bem-sucedida, uma rejeitada por saldo insuficiente e saldo final 1, sem valor negativo.

## Arquivo

`src/test/java/com/marceloaleixo/centrocirurgico/integration/PostgreSqlConcorrenciaBaixaEstoqueIT.java`

O teste usa uma tabela isolada `estoque_teste` e `SELECT ... FOR UPDATE`. Ele verifica o comportamento de concorrência do PostgreSQL, mas não instancia o serviço Spring `RastreabilidadeCirurgiaService` nem substitui um teste de integração ponta a ponta do domínio.

## Pré-requisitos

- JDK 21
- Docker Desktop em execução
- Acesso à internet para baixar a imagem `postgres:16-alpine` na primeira execução

## Executar no PowerShell

```powershell
.\mvnw.cmd "-Dtest=PostgreSqlConcorrenciaBaixaEstoqueIT" test
```

Depois, executar a suíte completa:

```powershell
.\mvnw.cmd test
```

## Resultado esperado

- 1 operação concluída;
- 1 operação recusada por saldo insuficiente;
- saldo final igual a `1.000`;
- nenhuma quantidade negativa.

## Limitação conhecida

Este é um teste de integração isolado da estratégia de lock no PostgreSQL. Ele não comprova, sozinho, que todas as rotas da aplicação usam os mesmos bloqueios, nem valida o fluxo FEFO real, entidades e movimentações da aplicação. Para isso, ainda é necessário um teste com o serviço real, os repositórios JPA e o schema completo.
