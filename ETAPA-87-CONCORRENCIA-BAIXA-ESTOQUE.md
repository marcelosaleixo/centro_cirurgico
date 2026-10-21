# Etapa 87 — Proteção contra concorrência na baixa de estoque

## Objetivo

Preservar a serialização de baixas simultâneas para o mesmo material e impedir regressões na regra FEFO.

## Proteções confirmadas no código

- `EstoqueGeralRepository.bloquearPorItemId` utiliza `PESSIMISTIC_WRITE` para bloquear a linha de saldo geral.
- `LoteEstoqueRepository.bloquearLotesValidosFefo` utiliza `PESSIMISTIC_WRITE`, seleciona apenas lotes com saldo positivo e validade a partir da data atual, e ordena por validade e ID.
- `RastreabilidadeCirurgiaService.registrar` é transacional e bloqueia primeiro o saldo geral e depois os lotes. Operações concorrentes para o mesmo item devem aguardar a liberação do bloqueio do saldo.
- Adicionados testes de contrato para garantir que os bloqueios e a ordenação FEFO não sejam removidos acidentalmente.

## Como executar no Windows PowerShell

```powershell
.\mvnw.cmd "-Dtest=EstoqueLockingContractTest,RastreabilidadeCirurgiaServiceTest" test
```

Depois, execute a suíte completa:

```powershell
.\mvnw.cmd test
```

## Limites desta etapa

Os novos testes são testes de contrato por reflexão e não simulam duas transações concorrentes reais contra PostgreSQL. Para comprovar o comportamento de ponta a ponta, ainda é necessário executar um teste de integração com duas transações simultâneas usando o serviço real e PostgreSQL/Testcontainers. Não execute testes de carga no banco hospitalar de produção.
