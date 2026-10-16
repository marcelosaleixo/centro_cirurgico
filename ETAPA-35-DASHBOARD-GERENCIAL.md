# Etapa 35 — Dashboard gerencial

## Objetivo
Disponibilizar uma visão consolidada dos principais indicadores operacionais, sem alterar dados do sistema.

## Indicadores
- Colaboradores ativos e inativos.
- Itens ativos de estoque.
- Itens ativos cujo saldo está igual ou abaixo do estoque mínimo (inclui item ativo sem saldo cadastrado, que precisa de conferência).
- Plantões entre a data atual e o último dia do mês corrente.
- Operações de auditoria registradas nos últimos sete dias.

## Acesso
- Rota: `/dashboard` (a rota `/` também abre o dashboard).
- Requer autenticação, conforme a configuração de segurança existente.
- Os links para módulos respeitam as permissões já aplicadas no servidor.

## Instalação e validação
1. Faça backup do projeto e do banco.
2. Extraia o ZIP em uma pasta separada e compare com a versão em uso.
3. Execute `./mvnw clean test` no Linux/macOS ou `\.\mvnw.cmd clean test` no PowerShell.
4. Se os testes passarem, inicie com `./mvnw spring-boot:run` ou `\.\mvnw.cmd spring-boot:run`.
5. Teste `/dashboard` com perfis ADMIN_RH, RH e CONSULTA e confirme os totais comparando com os cadastros.

## Observações
- Não há nova migração Flyway nesta etapa.
- O dashboard depende das tabelas e migrações das etapas anteriores, incluindo `auditoria_operacoes`.
- Os indicadores são contagens simples, não substituem relatórios oficiais nem decisões clínicas.
- A compilação Maven não foi confirmada neste ambiente porque o Maven Wrapper não conseguiu baixar sua distribuição. Execute os testes localmente antes de implantar.
