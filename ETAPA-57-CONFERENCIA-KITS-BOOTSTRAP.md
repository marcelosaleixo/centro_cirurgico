# Etapa 57 — Conferência de kits e Bootstrap 5

## Funcionalidades
- Tela de conferência por montagem, com progresso de itens e status.
- Registro individual de quantidade conferida, confirmação e observação.
- A conclusão exige todos os itens marcados como conferidos, quantidade informada e igualdade entre quantidade prevista e conferida.
- Se houver divergência, a montagem fica com status `DIVERGENCIA`; nenhuma baixa de estoque é feita.
- A tela de listagem de montagens passa a oferecer o botão **Conferir itens**.
- Bootstrap 5.3.3 foi adicionado ao fragmento global de cabeçalho para melhorar formulários, cartões, tabelas e responsividade. A tela de conferência usa classes Bootstrap.

## Rotas
- `GET /mutiroes/montagens-kits/{montagemId}/conferir`
- `POST /mutiroes/montagens-kits/{montagemId}/itens/{itemId}`
- `POST /mutiroes/montagens-kits/{montagemId}/concluir`

## Instalação
1. Faça backup do código e banco de dados.
2. Atualize o repositório com este pacote.
3. Faça o build/deploy usando Java 21 e a configuração já utilizada pelo projeto.
4. Não há nova migração nesta etapa: são utilizadas as tabelas criadas pela migração V20.
5. Abra um mutirão, entre no dia e acesse a montagem de kit; clique em **Conferir itens**.

## Observações importantes
- O Bootstrap CSS/JS é carregado pelo CDN jsDelivr; a interface precisa de acesso à internet no navegador para obter esses arquivos. O CSS original do projeto continua carregado depois do Bootstrap para manter a identidade visual.
- A conclusão só é permitida quando todas as quantidades conferidas correspondem às previstas. Divergências devem ser revisadas; não existe ajuste automático de quantidade nem movimentação de estoque.
- O ZIP foi montado e verificado estruturalmente. Não foi executado build Maven, teste de integração nem teste contra PostgreSQL real neste ambiente.
