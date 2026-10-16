# Correção direcionada do ClassFormatError

O log ainda apresenta `ClassFormatError: Invalid method Code length 0` ao carregar `entity.Colaborador`. Isso indica bytecode `.class` inválido no diretório `target/classes`; não é erro do PostgreSQL.

Nesta versão, `Colaborador` não depende mais de geração de getters/setters/constructor pelo Lombok. A configuração Maven também declara explicitamente o processador de anotações do Lombok para as demais classes.

## Obrigatório no Windows
Pare a aplicação e, na raiz do projeto, execute:

```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Se a IDE executar a classe sem passar pelo Maven, reimporte o projeto Maven e faça Rebuild Project. Não basta apenas trocar o código-fonte: é necessário remover o `.class` corrompido. Não apague nem recrie o banco de dados para corrigir este erro.
