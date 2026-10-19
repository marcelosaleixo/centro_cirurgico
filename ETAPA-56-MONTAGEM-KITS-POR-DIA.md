# Etapa 56 — Montagem de kits vinculada ao dia do mutirão

## Entregas
- Criação de uma montagem de kit a partir de um modelo ativo para um dia específico do mutirão.
- Cópia dos itens do modelo para uma lista de conferência independente, preservando a composição original mesmo se o modelo for alterado depois.
- Cálculo da quantidade prevista por item: quantidade do modelo × quantidade de kits solicitada.
- Registro do responsável e status inicial `PENDENTE`.
- Migração Flyway V20 com tabelas `montagem_kit_mutirao` e `item_conferencia_kit_mutirao`.
- Tela responsiva de criação/listagem em `/mutiroes/dias/{diaId}/montagens-kits`.

## Instalação
1. Faça backup do código e do PostgreSQL.
2. Atualize o repositório usando este pacote.
3. Faça deploy com Java 21 e confira nos logs a execução da migração V20.
4. Cadastre os modelos em `/mutiroes/kits`.
5. Abra `/mutiroes`, entre no evento, abra a programação diária e acesse a rota `/mutiroes/dias/{diaId}/montagens-kits` substituindo `{diaId}` pelo identificador do dia.

## Limites desta etapa
- Não baixa nem reserva estoque automaticamente.
- A tela cria a lista de conferência, mas o fluxo de marcar item conferido/encerrar montagem e a geração de solicitações de materiais são etapas seguintes.
- Antes de produção, validar build Maven, migração V20 e permissões no ambiente de homologação.
