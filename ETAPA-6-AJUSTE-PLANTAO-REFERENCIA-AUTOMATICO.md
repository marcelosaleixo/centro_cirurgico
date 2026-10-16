# Ajuste — Plantão de referência automático

## Regra
- Dia do mês ímpar (1, 3, 5, ..., 31) -> `IMPAR`
- Dia do mês par (2, 4, 6, ..., 30) -> `PAR`

## Alterações
- `templates/colaboradores/formulario.html`: ao alterar a data, o campo de plantão é atualizado automaticamente; a mesma regra é aplicada ao abrir novo cadastro e edição.
- `service/ColaboradorService.java`: aplica a regra também no servidor antes de salvar, evitando divergência caso o navegador não execute JavaScript ou envie outro valor.

## Teste
1. Reinicie a aplicação.
2. Abra `/colaboradores/novo`.
3. Selecione `15/09/2026`: deve preencher `IMPAR`.
4. Selecione `16/09/2026`: deve preencher `PAR`.
5. Abra a edição de um colaborador e confira se o plantão é recalculado pela data salva.
6. Salve e confirme a coluna Plantão de referência na listagem.

Não é necessário alterar o banco de dados.
