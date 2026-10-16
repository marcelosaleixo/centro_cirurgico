# Etapa 14 — Cadastro de feriados e integração com a escala

- CRUD de feriados: cadastrar, editar e inativar.
- Abrangências: nacional, estadual, municipal e ponto facultativo.
- Campos: nome, data, município, UF, recorrência anual, ativo e observação.
- A lista e o calendário mostram o dia da semana; se for feriado, também mostram o nome.
- A geração consulta o cadastro de feriados e adiciona a anotação `FERIADO: ...` à observação do plantão.
- Feriado não vira folga automaticamente; o plantão continua programado salvo regra operacional específica.
- Os feriados nacionais fixos e a Sexta-feira Santa calculada continuam disponíveis automaticamente.
- A migração Flyway V4 cria a tabela `feriado`. Não apagar o banco.

## Teste
1. Inicie a aplicação para executar a migração V4.
2. Abra `/feriados` e cadastre os feriados municipais/estaduais aplicáveis.
3. Gere uma escala para o período.
4. Confira `/escalas` e `/escalas/calendario`.

Recorrência anual reutiliza mês/dia da data cadastrada.
