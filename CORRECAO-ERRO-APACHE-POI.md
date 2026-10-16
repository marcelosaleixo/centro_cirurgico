# Correção — Apache POI ausente no classpath

O erro `NoClassDefFoundError: org/apache/poi/ss/usermodel/Workbook` indica que a classe principal do Apache POI não está disponível no classpath usado para iniciar a aplicação.

O `pom.xml` agora declara explicitamente:
- `org.apache.poi:poi:5.4.1`
- `org.apache.poi:poi-ooxml:5.4.1`

## Passos após extrair

1. Na IDE, faça Reload/Reimport do projeto Maven.
2. Feche a aplicação se ela estiver em execução.
3. No terminal, na pasta que contém `pom.xml`, rode:

```powershell
.\mvnw.cmd -U clean package -DskipTests
```

4. Inicie pela mesma pasta:

```powershell
.\mvnw.cmd spring-boot:run
```

Se for executar pela IDE, escolha a configuração Maven/Spring Boot depois de recarregar as dependências. Não execute apenas classes antigas da pasta `target`.

## Diagnóstico opcional

```powershell
.\mvnw.cmd dependency:tree -Dincludes=org.apache.poi
```

A árvore deve listar `poi` e `poi-ooxml` na versão 5.4.1. Nenhuma alteração no PostgreSQL é necessária.
