# Etapa 59 — Separação de materiais e medicamentos

## Objetivo

Registrar a separação item a item das solicitações de mutirão, identificar o responsável e bloquear a confirmação de entrega enquanto houver itens pendentes ou quantidades diferentes das solicitadas.

## Funcionalidades

- Quantidade separada por item, com validação entre zero e a quantidade solicitada.
- Identificação do responsável e data/hora do registro da separação.
- Indicador visual de itens completos e pendentes.
- Registro de responsável e data/hora ao confirmar a entrega.
- A entrega é recusada se qualquer item não tiver quantidade separada exatamente igual à quantidade solicitada.
- Migração Flyway V22 adiciona campos de rastreabilidade sem apagar os dados existentes.
- Layout responsivo com Bootstrap para a tela de detalhe.

## Instalação

1. Faça backup do código e do PostgreSQL.
2. Incorpore os arquivos do ZIP ao repositório existente.
3. Faça o deploy usando Java 21.
4. Confirme nos logs a execução da migração `V22__rastreabilidade_separacao_entrega_mutirao.sql`.
5. Acesse `/mutiroes/solicitacoes`, abra uma solicitação aprovada e inicie a separação.
6. Informe as quantidades separadas e o responsável e clique em **Salvar conferência**.
7. Após todos os itens estarem completos, informe o responsável pela entrega e confirme.

## Observação importante

Esta etapa registra separação e entrega no fluxo de solicitação, mas **não movimenta nem baixa estoque automaticamente**. A integração com movimentos de estoque deve ser implementada separadamente e validada com as regras do hospital. Quantidades parciais podem ser registradas para apontar divergências, mas impedem a confirmação de entrega final.

## Validação

O pacote deve ser validado com build Maven e testes em ambiente de homologação conectado a PostgreSQL antes da produção. Não foram simulados pedidos reais, movimentações de estoque nem entregas hospitalares.
