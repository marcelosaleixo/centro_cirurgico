# Etapa 54 — Equipes por dia e sala cirúrgica

## Objetivo
Aprimorar a montagem das equipes do mutirão, permitindo indicar a sala cirúrgica para cada colaborador e alertando quando o colaborador já possui plantão cadastrado na data selecionada.

## Implementação
- Atribuição de colaborador por dia, função no mutirão e sala opcional.
- Seleção de salas ativas já cadastradas no sistema.
- Exibição da sala na tabela da equipe diária.
- Verificação de existência de escala do colaborador na mesma data antes de gravar nova atribuição. Quando há escala, a inclusão é bloqueada e o usuário é orientado a revisar a escala.
- Migração Flyway `V18__equipe_mutirao_por_sala.sql`.

## Instalação
1. Faça backup do código e do PostgreSQL.
2. Atualize o projeto com os arquivos desta etapa.
3. Faça o deploy usando Java 21 e as variáveis de ambiente já existentes.
4. Confirme nos logs que o Flyway aplicou a migração V18.
5. Acesse `/mutiroes`, abra um evento e entre na programação diária para atribuir colaboradores por sala.

## Regras e limitações
- A sala é opcional para permitir equipes compartilhadas ou ainda não definidas.
- A verificação da escala é conservadora: se existir um registro de escala para o colaborador e data, a inclusão é bloqueada, sem tentar inferir disponibilidade pelo horário.
- Esta etapa não substitui validação formal da chefia nem altera a escala de RH.
- Não executa baixa de estoque nem solicita materiais.

## Validação
O ZIP é empacotado a partir do projeto completo da Etapa 53. Antes de produção, execute `./mvnw test` (Windows: `mvnw.cmd test`) e valide a migração em banco de homologação. Não declare o build ou teste de banco como concluído sem executar esses passos.
