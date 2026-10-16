# Correção — Data do plantão vazia ao editar

## Sintoma
Na tela `GET /escalas/{id}/editar`, colaborador, horários, tipo, status e observação aparecem preenchidos, mas a data do plantão fica vazia.

## Correção
- O Controller já possui `form.setDataPlantao(escala.getDataPlantao())`.
- O template `templates/escalas/formulario.html` agora formata explicitamente o valor para `yyyy-MM-dd`, formato exigido por `<input type="date">`.
- O campo mantém `name="dataPlantao"` para que o Spring faça o binding do POST para `LocalDate`.

## Validar
1. Pare a aplicação.
2. Recompile na raiz do projeto:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
```
3. Inicie com `.\mvnw.cmd spring-boot:run`.
4. Abra a edição de uma escala existente. A data deverá aparecer preenchida.

Se ainda ficar vazia, inspecione o valor de `escala.getDataPlantao()` no método `editar`; isso indicará que a data não está salva ou lida corretamente na entidade. Não é necessário apagar o banco.
