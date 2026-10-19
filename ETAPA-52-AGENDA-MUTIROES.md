# Etapa 52 — Agenda de Mutirões Cirúrgicos

## Entrega
- Cadastro de evento principal com especialidade, período, responsável, status e observações.
- Programação diária independente dentro do período do evento.
- Associação opcional de sala cirúrgica por dia.
- Equipe por dia, reutilizando colaboradores ativos já cadastrados no módulo de RH.
- Migração Flyway `V16__agenda_mutiroes_e_equipes_diarias.sql`.
- CRUD Thymeleaf responsivo usando os fragments e o menu já existentes.

## Requisitos
Java 21, Spring Boot 3.5.6, PostgreSQL e Flyway, conforme o projeto.

## Instalação
1. Faça backup do banco e do código antes de atualizar.
2. Substitua o projeto pela versão deste pacote preservando suas configurações de ambiente.
3. Confira `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente.
4. Faça o deploy/build. O Flyway executará a migração V16 automaticamente na inicialização.
5. Acesse `/mutiroes`, crie um evento e depois configure sua programação diária e equipes.

## Regras implementadas
- Data final não pode ser anterior à data inicial.
- Cada data pode ser cadastrada uma única vez por evento.
- A data programada deve estar dentro do período do evento.
- Colaboradores inativos não podem ser escalados.
- O mesmo colaborador pode participar de dias diferentes; a unicidade é por dia, colaborador e função no mutirão.
- As salas são opcionais na primeira etapa, permitindo planejar a agenda antes de definir a alocação.

## Escopo ainda não incluído
Esta etapa não cadastra as cirurgias individuais, pacientes, kits, materiais ou medicamentos. Essas partes serão adicionadas nas etapas seguintes. A validação de conflito com a escala de RH também deverá ser acrescentada antes de tratar a equipe como escala definitiva.

## Verificação
O ZIP preserva os arquivos da Etapa 51. A integridade do pacote foi verificada. O build Maven e a migração em PostgreSQL real precisam ser executados no ambiente de desenvolvimento/implantação antes de produção.
