# Etapa 5 — Gestão de escalas 12x36

## Funcionalidades
- Listagem de escalas por período, com filtro opcional por colaborador.
- Cadastro, edição e exclusão de escala.
- Geração automática de plantões em dias alternados, respeitando a data e o tipo de referência do colaborador.
- Bloqueio de colaborador inativo, datas anteriores à referência, datas fora da alternância 12x36 e períodos com férias solicitadas/aprovadas.
- Datas já cadastradas são ignoradas na geração automática.
- Carregamento antecipado dos relacionamentos usados na tabela para evitar `LazyInitializationException`.
- Link de Escalas adicionado ao menu.
- Corrigido import duplicado `java.util.List` no `ColaboradorRepository`.

## Rotas
- `GET /escalas`
- `GET /escalas/nova`
- `GET /escalas/{id}/editar`
- `POST /escalas/salvar`
- `POST /escalas/gerar`
- `POST /escalas/{id}/excluir`

## Regra de geração
Para cada colaborador, a geração usa a `dataReferenciaPlantao` cadastrada e cria uma escala a cada dois dias a partir dela, entre as datas informadas. O campo `tipoPlantao` deve corresponder ao `plantaoReferencia`. A rotina ignora datas existentes e datas que cruzam férias em status `SOLICITADA` ou `APROVADA`.

A geração usa horário padrão de 07:00 a 19:00. Ajuste conforme o horário real de plantão da instituição antes de produção.

## Execução
Na raiz do projeto:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Não apague o banco. Esta etapa não requer alteração de schema.
