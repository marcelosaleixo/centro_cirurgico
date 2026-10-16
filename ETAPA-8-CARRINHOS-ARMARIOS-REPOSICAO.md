# Etapa 8 — Gestão de carrinhos, armários e reposição

## Incluído
- CRUD de carrinhos de anestesia (cadastro, edição e ativar/desativar).
- CRUD de armários vinculados a uma sala cirúrgica (cadastro, edição e ativar/desativar).
- Associação de insumos a cada carrinho/armário e configuração da quantidade mínima.
- Visão de saldo atual versus mínimo para sinalizar necessidade de reposição.
- Seleção de sala por nome no cadastro de armário.
- A reposição física continua registrada pela movimentação de transferência do estoque geral para o recurso, mantendo o histórico e as validações transacionais.

## Rotas
- `GET /estoque/recursos`
- `GET /estoque/recursos/carrinhos/novo`
- `GET /estoque/recursos/carrinhos/{id}/editar`
- `POST /estoque/recursos/carrinhos/salvar`
- `POST /estoque/recursos/carrinhos/{id}/alternar`
- `GET /estoque/recursos/armarios/novo`
- `GET /estoque/recursos/armarios/{id}/editar`
- `POST /estoque/recursos/armarios/salvar`
- `POST /estoque/recursos/armarios/{id}/alternar`
- `GET /estoque/recursos/carrinhos/{id}/itens`
- `GET /estoque/recursos/armarios/{id}/itens`

## Teste
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Abra `/estoque/recursos`. Não é necessário apagar o banco. Compile e teste localmente antes de usar.
