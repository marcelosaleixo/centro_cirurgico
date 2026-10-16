# Correção — erro 400 ao gerar escala

## Causa
O controller recebia `inicio` e `fim` como `LocalDate` com formato obrigatório `yyyy-MM-dd`, mas a URL que provocou o erro enviou `01/10/2026` e `31/10/2026`. O Spring não consegue converter automaticamente esse formato para `LocalDate` usando o padrão ISO.

## Alteração
`EscalaPlantaoController` agora recebe as datas como texto e faz o parse aceitando:
- `yyyy-MM-dd` (formato esperado pelo `<input type="date">`);
- `dd/MM/yyyy` (formato legado presente no URL informado).

A geração continua validando o período pelo `EscalaPlantaoService`. URLs inválidas exibem mensagem amigável e a tela de consulta retorna ao mês atual. Redirecionamentos passam a usar formato ISO.

## Teste
1. Pare a aplicação.
2. Rode `.\mvnw.cmd clean package -DskipTests`.
3. Inicie `.\mvnw.cmd spring-boot:run`.
4. Teste `http://localhost:8080/escalas?inicio=01%2F10%2F2026&fim=31%2F10%2F2026&colaboradorId=2`.
5. Abra a tela de escalas e gere o período pelo formulário.

Não apague nem recrie o banco PostgreSQL.
