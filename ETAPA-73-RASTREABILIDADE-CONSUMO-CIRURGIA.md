# Etapa 73 — Rastreabilidade de consumo por cirurgia

## Entrega
- Tela `/estoque/rastreabilidade-cirurgias` para registrar consumo de material vinculado a uma cirurgia.
- Baixa por FEFO usando lotes válidos em ordem de vencimento.
- Atualização transacional do saldo geral, saldo dos lotes, histórico de consumo por cirurgia e movimentações de saída.
- Consulta de histórico global ou filtrado por cirurgia.
- Migração Flyway `V28__rastreabilidade_consumo_por_cirurgia.sql`.

## Regras
- Cirurgia, material, quantidade positiva e observação são obrigatórios.
- Materiais inativos não podem ser consumidos.
- A operação é recusada se o saldo geral ou os lotes válidos forem insuficientes.
- Lotes vencidos não são selecionados pela rotina FEFO.
- Se qualquer parte da operação falhar, a transação deve ser revertida.

## Como validar
1. Faça backup do banco e do projeto.
2. Inicie a aplicação e confirme a execução da migração V28.
3. Acesse Estoque → Rastreabilidade por cirurgia pela URL `/estoque/rastreabilidade-cirurgias`.
4. Registre um consumo que caiba em um lote e outro que precise de mais de um lote.
5. Confirme o histórico da cirurgia, os saldos dos lotes, o estoque geral e as movimentações.

## Observação
A tela usa a entidade `Cirurgia` do cadastro de cirurgias regulares. Cirurgias de mutirão (`CirurgiaMutirao`) são entidades diferentes e não são incluídas nesta rastreabilidade nesta etapa. A compilação e a integração com PostgreSQL precisam ser executadas no ambiente do projeto antes de produção.
