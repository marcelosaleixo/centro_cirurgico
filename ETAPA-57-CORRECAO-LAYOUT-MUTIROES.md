# Correção visual do módulo de mutirões

## O que mudou
- Adicionados estilos locais de contingência para botões, formulários, tabelas, cartões e alertas do módulo de mutirões.
- Padronizados cabeçalhos e espaçamentos das páginas do módulo.
- Formulários organizados em duas colunas em telas largas e uma coluna em celulares.
- Atualizada a versão do CSS para evitar cache do navegador.
- Mantidos os arquivos e funcionalidades anteriores, incluindo a correção das datas de edição do mutirão.

## Aplicação
1. Substitua os arquivos do projeto pelos arquivos deste pacote e faça commit/push.
2. Reconstrua e publique a aplicação.
3. No navegador, faça recarga forçada (Ctrl+F5).
4. Confira a listagem e o formulário de edição em `/mutiroes` e `/mutiroes/1/editar`.

Os estilos locais não dependem do carregamento do Bootstrap CDN para os elementos principais do módulo. O restante do sistema mantém os estilos existentes.

## Validação
O pacote ZIP foi verificado quanto à integridade. Não foi executado build Maven nem teste integrado em navegador/PostgreSQL.
