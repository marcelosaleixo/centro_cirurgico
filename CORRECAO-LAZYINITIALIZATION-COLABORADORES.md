# Correção — LazyInitializationException em colaboradores

## Causa
A página `colaboradores/lista.html` acessa `c.funcao.nome` depois que a sessão Hibernate já foi encerrada. O relacionamento `Colaborador.funcao` é carregado de forma lazy, então o Thymeleaf não consegue inicializar o proxy.

## Correção
Foi adicionada ao `ColaboradorRepository` uma consulta `findAllComRelacionamentos()` com `@EntityGraph(attributePaths = {"funcao", "equipe"})`, para buscar os relacionamentos necessários no mesmo contexto de persistência. O método de listagem foi atualizado para usar essa consulta.

## Executar
Na raiz do projeto:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Não apague o banco de dados.
