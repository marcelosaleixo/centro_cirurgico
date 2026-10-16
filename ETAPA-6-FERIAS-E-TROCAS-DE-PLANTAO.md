# Etapa 6 — Gestão de férias e trocas de plantão

- Cadastro de solicitação de férias, validação de período e sobreposição, aprovação, rejeição e cancelamento.
- Solicitação de troca de plantão, validação de solicitante/aceitante ativos e da mesma função, aprovação, rejeição e cancelamento.
- Listagens com status e responsável pela decisão.
- Repositórios usam EntityGraph para carregar os relacionamentos apresentados nas telas.
- Menu atualizado com Férias e Trocas.

## Rotas
- GET /ferias ; GET /ferias/nova ; POST /ferias/solicitar
- POST /ferias/{id}/aprovar ; POST /ferias/{id}/rejeitar ; POST /ferias/{id}/cancelar
- GET /trocas ; GET /trocas/nova ; POST /trocas/solicitar
- POST /trocas/{id}/aprovar ; POST /trocas/{id}/rejeitar ; POST /trocas/{id}/cancelar

## Segurança
Nesta etapa o usuário seleciona o colaborador responsável pela decisão. Isso registra o nome no histórico, mas não substitui autenticação. Antes do uso real, proteja as rotas com Spring Security e derive o aprovador do usuário autenticado.

## Execução
Na raiz do projeto:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Não é necessário apagar ou recriar o PostgreSQL.
