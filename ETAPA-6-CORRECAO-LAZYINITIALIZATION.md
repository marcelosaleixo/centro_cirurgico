# Correção — LazyInitializationException na tela de troca de plantão

## Causa
A tela `trocas/formulario.html` acessa `c.funcao.nome`, mas a associação `Colaborador.funcao` é `LAZY`. O método `findByAtivoTrueOrderByNomeAsc()` carregava os colaboradores sem inicializar `funcao`, causando `LazyInitializationException` durante a renderização do Thymeleaf.

## Correção
No `ColaboradorRepository`, o método agora usa:

```java
@EntityGraph(attributePaths = {"funcao", "equipe"})
List<Colaborador> findByAtivoTrueOrderByNomeAsc();
```

Assim, função e equipe são carregadas junto com os colaboradores para as telas que usam essa consulta. Não é necessário mudar o `FetchType.LAZY` da entidade nem alterar o banco.

## Teste
1. Pare a aplicação.
2. Execute `.\mvnw.cmd clean package -DskipTests`.
3. Inicie com `.\mvnw.cmd spring-boot:run`.
4. Abra `http://localhost:8080/trocas/nova`.
5. Confirme que a lista de colaboradores aparece com o nome e a função profissional.

Se o erro persistir, envie o novo stack trace completo.
