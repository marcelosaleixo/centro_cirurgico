# Etapa 37 — Alertas operacionais

## Objetivo

Acrescentar ao dashboard alertas informativos para situações que merecem conferência humana, usando os dados existentes. Não há nova tabela nem migração de banco nesta etapa.

## Alertas disponíveis

- Itens ativos com saldo não informado ou saldo menor/igual ao estoque mínimo (exibe até 10 itens individualmente).
- Plantões dos últimos sete dias anteriores à data atual que ainda constam como `PROGRAMADO`.
- Resumo de plantões programados nos próximos sete dias.

Os alertas de alta prioridade aparecem antes dos informativos. Cada alerta inclui link para o módulo correspondente. Os alertas não modificam estoque nem status de escalas.

## Instalação e validação

1. Faça backup do projeto e do banco.
2. Extraia o pacote em uma pasta separada.
3. Execute `./mvnw clean test` (Windows: `.\mvnw.cmd clean test`).
4. Se os testes passarem, execute `./mvnw spring-boot:run` (Windows: ` .\mvnw.cmd spring-boot:run`).
5. Abra `/dashboard`, confirme alertas para estoque baixo e escalas, e teste os links para `/estoque` e `/escalas`.

## Limitações

- A etapa verifica apenas dados disponíveis nos repositórios existentes; não confirma falta real de material, cobertura de equipe nem pendências administrativas que não tenham regra explícita no sistema.
- A lista individual de itens críticos é limitada aos primeiros 10 por nome para manter o dashboard conciso.
- A compilação Maven precisa ser executada no ambiente do projeto antes da implantação.
