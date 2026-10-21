# Etapa 96 — Testes das regras de estoque, recursos e cirurgias de mutirão

## Objetivo
Adicionar testes unitários de regressão para regras críticas já implementadas nos serviços, sem alterar controllers, telas ou regras de produção.

## Cobertura adicionada

### `RecursosEstoqueServiceRegrasTest`
- Rejeita código duplicado de carrinho.
- Rejeita quantidade mínima negativa antes de consultar os repositórios.
- Impede vincular armário a sala cirúrgica inativa.

### `CirurgiaMutiraoServiceRegrasTest`
- Rejeita horário final igual ao horário inicial.
- Rejeita dia do mutirão fora do período do evento.
- Rejeita sobreposição de horários no mesmo dia.

## Execução

```powershell
.\mvnw.cmd -Dtest=RecursosEstoqueServiceRegrasTest,CirurgiaMutiraoServiceRegrasTest test
```

Depois, execute a suíte completa:

```powershell
.\mvnw.cmd clean test
```

## Limites desta etapa
São testes unitários com Mockito: não inicializam PostgreSQL, não comprovam transações reais nem concorrência entre requisições. As regras testadas dependem dos métodos e contratos atuais dos serviços. Para validação operacional completa, ainda é necessário executar os testes e validar as migrações no ambiente PostgreSQL.
