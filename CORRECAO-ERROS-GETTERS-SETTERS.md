# Correção dos erros de getters e setters

Removidas as anotações Lombok das entidades e adicionados construtores sem argumentos e getters/setters Java explícitos, para que a IDE reconheça métodos usados pelos services sem depender do processamento de anotações.

No Windows, na raiz do projeto, execute:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
