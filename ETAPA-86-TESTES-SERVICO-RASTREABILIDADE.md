# Etapa 86 — Testes do serviço de rastreabilidade cirúrgica

## Objetivo

Expandir os testes do serviço real `RastreabilidadeCirurgiaService` para cobrir a regra FEFO e a proteção de consumo em cirurgia com fechamento financeiro aprovado.

## Cenários incluídos

- Distribuição da baixa entre lotes em ordem de validade (FEFO), atualizando os saldos dos lotes e o estoque geral.
- Recusa quando o saldo total dos lotes válidos é insuficiente, sem salvar consumos, movimentações ou estoque geral.
- Interrupção pelo bloqueio financeiro antes de consultar/alterar os repositórios de estoque.

Os testes exercitam a implementação real do serviço, usando Mockito para isolar os repositórios. Não substituem testes de integração com banco real; esses continuam sendo cobertos separadamente pela etapa anterior.

## Executar no Windows PowerShell

```powershell
.\\mvnw.cmd "-Dtest=RastreabilidadeCirurgiaServiceTest" test
```

Para executar toda a suíte:

```powershell
.\\mvnw.cmd test
```

## Critérios esperados

- O lote com validade mais próxima é consumido primeiro.
- Lotes posteriores só são utilizados se o primeiro não cobrir toda a quantidade.
- Falta de lotes válidos não altera saldos nem cria registros de consumo/movimentação.
- Fechamento aprovado interrompe o fluxo antes de alterações no estoque.

## Limitação

A etapa acrescenta testes unitários do serviço com dependências simuladas. A compilação e execução Maven devem ser confirmadas no ambiente do projeto; não declarar os testes como aprovados sem executar os comandos.
