# Etapa 90 — Filtros e exportação do Dashboard de Suprimentos

## Entregas
- Filtro por status da solicitação.
- Filtro por data inicial e final de criação.
- Recalculo dos indicadores e da lista recente conforme o filtro aplicado.
- Exportação CSV dos resultados filtrados, com totais solicitados, separados e pendentes por solicitação.
- CSV em UTF-8 com BOM para melhorar a abertura de acentos no Excel.
- Campos responsivos para desktop e celular.
- Validação amigável quando a data inicial for posterior à final.

## Arquivos alterados
- `src/main/java/com/marceloaleixo/centrocirurgico/controller/DashboardSuprimentosController.java`
- `src/main/resources/templates/mutiroes/dashboard-suprimentos.html`

## Como usar
Acesse `/mutiroes/dashboard-suprimentos`, selecione status e/ou período e clique em **Aplicar filtros**. O botão **Exportar CSV** exporta o conjunto filtrado.

## Validação
A integridade do pacote ZIP foi verificada. A compilação Maven não pôde ser executada neste ambiente porque não há Maven instalado e o Maven Wrapper não pôde ser executado no ambiente Linux. Execute ` .\mvnw.cmd clean compile` no Windows PowerShell para validar no ambiente do projeto.
