# Etapa 7 — Gestão de estoque e insumos

## Incluído
- Cadastro e edição de itens de estoque com código, nome, unidade, estoque mínimo e quantidade inicial.
- Saldo do estoque geral por item.
- Entrada, saída/consumo e transferência entre estoque geral, carrinhos de anestesia e armários de sala.
- Regras transacionais: saldo não pode ficar negativo; movimentação requer quantidade positiva; itens inativos não podem ser movimentados.
- Histórico das últimas 100 movimentações, com tipo, quantidade, origem/destino, colaborador e motivo.
- Indicador de reposição quando o saldo do estoque geral está no mínimo ou abaixo.
- Listagem de carrinhos e armários já cadastrados, incluindo contagem de itens.
- Reutiliza entidades e tabelas existentes; não requer migração adicional.

## Rotas
- `GET /estoque`
- `GET /estoque/itens/novo`
- `GET /estoque/itens/{id}/editar`
- `POST /estoque/itens/salvar`
- `POST /estoque/itens/{id}/desativar`
- `GET /estoque/movimentacoes/nova`
- `POST /estoque/movimentacoes/salvar`

## Observação
Para uma transferência, informe o ID do carrinho ou armário como origem/destino quando aplicável. A página lista os IDs nos seletores de entidade? Nesta primeira versão, os campos de ID são manuais; uma etapa subsequente pode substituí-los por seletores com nomes e incluir CRUD de carrinhos/armários.

## Executar
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
A aplicação usa Flyway e Hibernate `ddl-auto=validate`. Não apague o banco. O pacote precisa ser compilado e testado no ambiente local antes de uso.
