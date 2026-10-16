# Correção — HTTP 400 no filtro de escalas

## Causa
O controller espera `LocalDate` no formato ISO `yyyy-MM-dd`, mas a requisição recebeu `01/10/2026` (formato `dd/MM/yyyy`). Isso causa `MethodArgumentTypeMismatchException` e HTTP 400.

## Alterações
- `EscalaPlantaoController`: parâmetros de data usam explicitamente `@DateTimeFormat(pattern = "yyyy-MM-dd")`.
- `templates/escalas/lista.html`: campos de data são renderizados explicitamente como `yyyy-MM-dd`, formato necessário para `input type="date"` e submissão de filtros.

## Teste
1. Pare a aplicação.
2. Na raiz do projeto, execute:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
3. Acesse `http://localhost:8080/escalas` e filtre um período.
4. Confira na URL que os parâmetros estão no formato `inicio=2026-10-01&fim=2026-10-31`, não `01/10/2026`.

Não é necessário alterar o banco de dados.
