# Etapa 55 — Modelos de kits cirúrgicos e melhoria visual

## Entrega
- Cadastro/listagem/edição de modelos reutilizáveis de kits cirúrgicos.
- Itens categorizados em material, fio, medicamento e outro.
- Quantidade por kit e unidade de medida.
- Vínculo opcional com item já cadastrado no estoque.
- Ativar/desativar modelos sem excluir o histórico de composição.
- Migração Flyway `V19__modelos_kits_cirurgicos.sql`.
- Atalho no menu lateral: **Modelos de kits cirúrgicos**.
- Refinamento visual responsivo do módulo de mutirões, com cabeçalho, cartões de resumo, tabela, formulários e tabela de itens adaptada para telas pequenas.

## Acesso
Abra **Mutirões cirúrgicos → Modelos de kits cirúrgicos** ou acesse `/mutiroes/kits`.

## Instalação
1. Faça backup do código e do PostgreSQL.
2. Atualize o repositório com os arquivos deste pacote.
3. Mantenha as variáveis de ambiente existentes e faça o deploy usando Java 21.
4. Confira nos logs a execução da migração Flyway `V19`.
5. Acesse `/mutiroes/kits` e cadastre um modelo de teste.

## Regras e limites desta etapa
- O kit é um **modelo**: não gera automaticamente solicitação de materiais, reserva, separação, consumo ou baixa no estoque.
- O vínculo ao estoque é opcional para permitir cadastrar fios/medicamentos que ainda não tenham cadastro no catálogo.
- A quantidade precisa ser maior que zero.
- Os documentos fornecidos de bariátrica e vascular servem como referência para cadastrar os modelos manualmente. Não foram importados como dados automaticamente.
- Antes da produção, testar em homologação: build Maven, migração Flyway, criação/edição de kits e relação com itens existentes. Não houve acesso ao PostgreSQL real deste ambiente.

## Próxima etapa sugerida
Etapa 56 — Instâncias de kits por mutirão/dia, cálculo das quantidades conforme o número de cirurgias, checklist de montagem e conferência, sem baixa automática de estoque até haver confirmação de entrega/consumo.
