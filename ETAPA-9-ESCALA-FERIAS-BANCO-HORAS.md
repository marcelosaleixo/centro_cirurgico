# Etapa 9 — Integração de férias e banco de horas na escala 12x36

## Regras implementadas
- A geração percorre as datas de plantão 12x36 do colaborador.
- Datas cobertas por férias `SOLICITADA` ou `APROVADA` são geradas/atualizadas com status `FERIAS`, com observação contendo o período e o status da solicitação.
- Datas com lançamento ativo no banco de horas são geradas/atualizadas com status `BANCO_HORAS`.
- Datas já cadastradas não são duplicadas; se já existirem como `PROGRAMADO`, a geração atualiza o status quando encontrar férias ou banco de horas.
- Se férias e banco de horas estiverem ativos na mesma data, a geração interrompe a operação e informa a data conflitante.
- O lançamento de banco de horas exige data de plantão válida segundo a referência 12x36, quantidade de horas entre 0,01 e 24, sem conflito com férias ou outro lançamento ativo.
- Ao lançar banco de horas para uma escala já existente, a escala é marcada imediatamente como `BANCO_HORAS`. Ao cancelar o lançamento, a escala volta a `PROGRAMADO` quando estava marcada como banco de horas.
- A página `/banco-horas` permite lançar, consultar, editar e cancelar lançamentos.

## Migração
Foi adicionada `V2__banco_horas_e_status_ausencias.sql`, que amplia os status válidos de `escala_plantao` e cria a tabela `banco_horas`. Não remova nem altere manualmente o histórico do banco.

## Como testar
1. Inicie a aplicação e confira se o Flyway executa a migração V2.
2. Cadastre férias solicitadas/aprovadas para um colaborador.
3. Abra `/escalas`, gere o período correspondente e confirme as linhas com status `FERIAS`.
4. Abra `/banco-horas`, lance uma data que coincida com um dia de plantão 12x36 e confira a escala como `BANCO_HORAS`.
5. Teste cancelamento de banco de horas e tente registrar férias e banco de horas para a mesma data.

## Compilação
```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
A compilação não foi validada neste ambiente porque o Maven Wrapper não conseguiu baixar o Maven do repositório externo. Execute os comandos no ambiente local para validar dependências e integração com o PostgreSQL.
