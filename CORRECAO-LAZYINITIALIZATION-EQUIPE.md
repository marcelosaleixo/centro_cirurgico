# Correção — LazyInitializationException na lista de equipes

## Causa
A tela `equipes/lista.html` chamava `e.colaboradores.size()` depois que a sessão Hibernate já estava encerrada. Como `Equipe.colaboradores` é uma coleção lazy, o Thymeleaf não podia inicializá-la.

## Alterações
- Adicionado `countByEquipeId(Long equipeId)` ao `ColaboradorRepository`.
- `EquipeController` calcula a quantidade por consulta de contagem e envia um mapa ao template.
- O template usa `quantidadeColaboradores[e.id]`, sem acessar a coleção lazy.

## Executar
Pare a aplicação e rode na raiz do projeto:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Não é necessário alterar ou apagar dados do PostgreSQL.
