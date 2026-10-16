# Correção — Data de referência não aparece ao editar colaborador

## Causa provável
O campo `input type="date"` exige o valor em formato ISO `yyyy-MM-dd`. Embora o Controller preencha `form.dataReferenciaPlantao`, a exibição do valor no formulário pode não estar sendo renderizada corretamente pelo binding atual.

## Correção
No template `templates/colaboradores/formulario.html`, o campo foi alterado para definir explicitamente:
- `name="dataReferenciaPlantao"` para manter o binding no POST;
- `th:value` formatado com `#temporals.format(..., 'yyyy-MM-dd')`, formato aceito pelo input date.

O Controller continua preenchendo `form.setDataReferenciaPlantao(c.getDataReferenciaPlantao())` no modo de edição e salvando `c.setDataReferenciaPlantao(form.getDataReferenciaPlantao())`.

## Validar
1. Pare a aplicação.
2. Recompile:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
```
3. Inicie e abra `/colaboradores/1/editar` (use o ID real do colaborador).
4. Confirme que a data aparece no campo e que continua preenchida após salvar.

Não é necessário alterar nem apagar o banco de dados.
