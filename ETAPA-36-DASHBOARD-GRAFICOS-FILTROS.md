# Etapa 36 — Dashboard com gráficos e filtros por período

## Implementação
- Filtros de data inicial e final no dashboard.
- Indicadores agregados de auditoria, movimentações de estoque e escalas para o intervalo selecionado.
- Evolução mensal comparativa usando barras HTML/CSS, sem adicionar dependência JavaScript externa.
- Mantidos os indicadores gerais da Etapa 35 e os acessos rápidos.
- Datas invertidas são normalizadas e consultas com intervalo excessivo são limitadas a três anos.
- Nenhuma nova migração de banco é necessária.

## Arquivos principais
- `DashboardController.java`
- `AuditoriaOperacaoRepository.java`
- `MovimentacaoEstoqueRepository.java`
- `EscalaPlantaoRepository.java`
- `templates/dashboard.html`

## Teste local
Execute na raiz do projeto:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Acesse `/dashboard`, escolha datas e confirme se os indicadores e as barras mudam conforme o período.

## Observação
A compilação e os testes precisam ser executados no ambiente de desenvolvimento antes da publicação. O gráfico consulta contagens agregadas do banco e não altera dados operacionais.
