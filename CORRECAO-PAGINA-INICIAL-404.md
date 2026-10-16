# Correção da página inicial (HTTP 404)

A rota inicial foi declarada diretamente na classe principal `CentrocirurgicoApplication`, dentro do pacote base que o Spring Boot escaneia. A rota `/` redireciona para `/colaboradores`. O `HomeController` separado foi removido para evitar mapeamentos duplicados.

## Aplicar
1. Pare a aplicação.
2. Extraia esta versão em uma pasta nova (ou substitua os arquivos correspondentes no projeto atual).
3. Na raiz do projeto, execute no PowerShell:

```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

4. Abra `http://localhost:8080/`. A aplicação deve redirecionar para `/colaboradores`.

Se `/` ainda retornar 404, confira no console se a aplicação iniciada está na pasta atual e se o processo foi reiniciado. O 404 normalmente indica que o servidor está executando uma versão antiga ou que o controller não foi registrado.
