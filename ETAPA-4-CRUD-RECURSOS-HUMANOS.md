# Etapa 4 — CRUD inicial de Recursos Humanos

Implementado:
- Dependência Thymeleaf.
- Página inicial redireciona para `/colaboradores`.
- CRUD de funções profissionais: listar, cadastrar, editar e excluir (com validação de vínculos no service).
- CRUD de equipes: listar, cadastrar, editar e excluir (bloqueia exclusão se houver colaboradores).
- Cadastro/edição/listagem de colaboradores com pesquisa por nome, CPF e e-mail.
- Desativação lógica de colaborador, preservando histórico.
- Formulários responsivos com CSS compartilhado.
- A entidade `Colaborador` inclui `isAtivo()`, `getAtivo()` e `setAtivo()`.

## Rotas
- `/colaboradores`
- `/colaboradores/novo`
- `/equipes`
- `/equipes/nova`
- `/funcoes`
- `/funcoes/nova`

## Execução
Configure o PostgreSQL em `application.properties` ou variáveis de ambiente e execute:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

O banco precisa estar com as migrations aplicadas. Não exclua o banco para esta etapa.
