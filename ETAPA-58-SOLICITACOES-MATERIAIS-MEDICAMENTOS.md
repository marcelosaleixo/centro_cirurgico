# Etapa 58 — Solicitações de materiais e medicamentos por mutirão

## Funcionalidades
- Solicitações associadas a um dia de mutirão.
- Itens livres ou vinculados ao catálogo do estoque, com categoria, quantidade e unidade.
- Fluxo de status: RASCUNHO → ENVIADA → APROVADA → EM_SEPARACAO → ENTREGUE.
- Registro do responsável pela aprovação e data/hora.
- Tela de listagem, formulário responsivo e detalhe da solicitação.
- Migração Flyway V21 cria `solicitacao_mutirao` e `item_solicitacao_mutirao`.

## Instalação
1. Faça backup do código e banco.
2. Atualize o repositório com os arquivos desta etapa.
3. Faça deploy com Java 21.
4. Verifique nos logs a execução da migração V21.
5. Acesse `/mutiroes/solicitacoes`.

## Limites importantes
Esta etapa registra pedidos e estados operacionais, mas não reserva, movimenta nem baixa estoque. A quantidade separada ainda é exibida como informação e não é editada item a item nesta primeira versão. Não há integração automática com farmácia nem geração automática a partir dos kits. Antes de produção, testar autorização/perfis, fluxo e migração em homologação.
