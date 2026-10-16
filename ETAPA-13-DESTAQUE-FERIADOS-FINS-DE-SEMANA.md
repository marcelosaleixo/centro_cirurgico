# Etapa 13 — Destaque de feriados e fins de semana na tela de escalas

## Alterações
- A data do plantão fica em vermelho claro quando for feriado nacional listado.
- A data fica em azul claro quando cair em sábado ou domingo.
- A célula mostra o nome do feriado ou o rótulo "Fim de semana".
- O título da célula também informa a classificação.
- A classificação é aplicada na lista de escalas após os filtros de período.
- Não altera a geração, os horários, nem os dados persistidos.

## Feriados incluídos
- 1º de janeiro, 21 de abril, 1º de maio, 7 de setembro, 12 de outubro,
  2 de novembro, 15 de novembro, 20 de novembro e 25 de dezembro.
- Sexta-feira Santa, calculada a cada ano.

**Nota:** feriados estaduais e municipais não são inferidos automaticamente; podem ser incluídos posteriormente conforme o calendário oficial aplicável ao hospital.

## Teste
```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Abra `/escalas` e filtre um período que contenha sábado, domingo e feriado nacional.
