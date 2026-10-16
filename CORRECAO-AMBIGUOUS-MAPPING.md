# Correção: Ambiguous mapping para GET /

## Causa
A rota `/` estava registrada duas vezes:
- `CentrocirurgicoApplication.inicio()`
- `HomeController.inicio()`

O Spring MVC não permite dois handlers com o mesmo método HTTP e caminho.

## Correção
A classe principal voltou a conter somente `main()`. A rota `/` fica exclusivamente em `controller/HomeController.java`, redirecionando para `/colaboradores`.

## Recompilar
Pare a aplicação e, na raiz do projeto, execute:

```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Depois acesse `http://localhost:8080/`. O banco de dados não precisa ser apagado.
