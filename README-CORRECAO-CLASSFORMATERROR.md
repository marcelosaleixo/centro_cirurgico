# Correção do erro ClassFormatError

Erro reportado:
`java.lang.ClassFormatError: Invalid method Code length 0 in class file ... entity/Colaborador`

Esse erro ocorre ao carregar bytecode `.class` inválido, antes de o Spring validar o banco. Não é um erro SQL.

Ajustes nesta versão:
- Lombok fixado em 1.18.42, com suporte ao Java 21.
- Annotation processor do Lombok declarado explicitamente no Maven Compiler Plugin.

## Passos no Windows
1. Pare a aplicação.
2. Feche a IDE ou desative temporariamente o auto-build.
3. Na pasta do projeto, execute:
   `mvnw.cmd clean package -DskipTests`
4. Se a compilação terminar com BUILD SUCCESS, execute:
   `mvnw.cmd spring-boot:run`
5. No IntelliJ/Eclipse, reimporte o projeto Maven e faça rebuild caso a IDE continue executando classes antigas.

Se o erro persistir, apague a pasta `target` depois de parar a aplicação e repita a compilação. Não apague o banco de dados.
