# Etapa 16 — Conferência de escalas e Spring Boot DevTools

## Funcionalidades
- Relatório operacional por intervalo em `/relatorios/fechamento-rh`.
- Agrupa escalas por colaborador e resume plantões programados/realizados, horas, férias, banco de horas, cancelamentos e escalas em finais de semana.
- Identifica feriados apenas quando a observação da escala contém a referência ao feriado. Isso não substitui a integração completa com o cadastro de feriados.
- Link para o relatório no menu principal.
- Spring Boot DevTools para reinicialização automática durante o desenvolvimento e LiveReload quando suportado pela IDE/navegador.

## DevTools
A dependência foi adicionada ao `pom.xml` com `scope` `runtime` e `optional=true`, prática recomendada para não incluí-la como dependência transitiva de outros projetos.

As propriedades `spring.devtools.restart.enabled=true`, `spring.devtools.livereload.enabled=true` e `spring.thymeleaf.cache=false` foram adicionadas ao `application.properties` para o ambiente local.

Para que o restart funcione, execute a aplicação pela IDE ou por `mvn spring-boot:run` e habilite a compilação automática/build automático da IDE. DevTools não deve ser usado como ferramenta de produção.

## Executar
```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Abra `http://localhost:8080/relatorios/fechamento-rh`.

## Observações importantes
- Nenhuma migração de banco é necessária nesta etapa.
- As horas são calculadas a partir dos horários de início/fim e descontam o intervalo quando os dois horários do intervalo estão preenchidos.
- O relatório é operacional e não deve ser usado diretamente como folha de pagamento sem validação das regras do RH.
- A compilação Maven não foi confirmada neste ambiente porque o Maven Wrapper não conseguiu baixar a distribuição do Maven por indisponibilidade de rede.
