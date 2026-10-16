# Centro Cirúrgico Hospitalar — Etapa 2

Projeto original `centrocirurgico-v1` estendido, mantendo o pacote `com.marceloaleixo.centrocirurgico`.

## O que foi implementado
- Repositórios Spring Data JPA para funções, equipes, colaboradores, escalas, férias e trocas de plantão.
- Serviços transacionais para as mesmas áreas.
- Validação de duplicidade de nome, CPF e e-mail; vínculo válido de função/equipe; escala sem férias no dia; férias sem sobreposição; troca entre colaboradores ativos da mesma função; aprovação/rejeição de férias e trocas apenas quando pendentes.
- Flyway com a estrutura inicial PostgreSQL em `src/main/resources/db/migration/V1__estrutura_inicial.sql`.
- Hibernate em `ddl-auto=validate` para detectar divergências entre entidades e banco.
- Projeto alinhado a Spring Boot 3.5.6 e Java 21.

## Configuração local
Crie o banco `centro_cirurgico` no PostgreSQL ou ajuste as variáveis:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SHOW_SQL` (opcional; padrão `false`)

## Executar
Windows: `mvnw.cmd spring-boot:run`
Linux/macOS: `./mvnw spring-boot:run`

Teste: `mvnw.cmd test` ou `./mvnw test`.

## Observações importantes
- O serviço de escala valida o tipo PAR/ÍMPAR pelo dia do mês e bloqueia plantão em férias solicitadas ou aprovadas. Confirme se a regra operacional do hospital define paridade dessa forma antes de colocar em produção.
- A trigger PostgreSQL reforça a regra de mesma função na troca de plantão.
- Conflitos de horário entre salas/profissionais e integração automática de uma troca aprovada com a escala serão tratados em uma etapa específica, com transação e proteção contra concorrência.
- Os serviços estão prontos para futura camada MVC/REST; não foram criados Controllers nem telas nesta etapa.
