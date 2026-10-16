# Etapa 17 — Exportação do fechamento mensal do RH para Excel

## Implementação
- Adicionada a dependência Apache POI (`poi-ooxml`) para gerar arquivos `.xlsx`.
- Adicionado `FechamentoRhExcelService`, que reutiliza os resumos já calculados pelo `FechamentoRhService`.
- Adicionado endpoint `GET /relatorios/fechamento-rh/excel?inicio=AAAA-MM-DD&fim=AAAA-MM-DD`.
- Adicionado botão **Exportar Excel** ao relatório de conferência do RH.
- O arquivo inclui período, aviso de uso operacional, cabeçalho formatado, colunas dimensionadas e filtro automático.
- Mantido o Spring Boot DevTools da etapa anterior.

## Teste local
1. Execute `./mvnw clean package` (Windows: `mvnw.cmd clean package`).
2. Inicie com `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
3. Acesse `/relatorios/fechamento-rh`, selecione as datas e clique em **Exportar Excel**.
4. Confirme que o arquivo `.xlsx` abre no Excel ou LibreOffice e que os números correspondem à tabela exibida na tela.

## Observação
O relatório é uma conferência operacional; valide as regras de jornada, feriados, substituições e banco de horas com o RH antes de utilizá-lo para folha de pagamento. A compilação precisa ser confirmada no ambiente do projeto, pois o download das dependências Maven depende da rede local.
