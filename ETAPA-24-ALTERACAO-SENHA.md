# Etapa 24 — Alteração segura de senha

- Adiciona a tela `/minha-conta/senha`, disponível somente a usuários autenticados.
- Exige a senha atual.
- Exige nova senha com pelo menos 12 caracteres e confirmação idêntica.
- Não permite reutilizar a senha atual.
- Salva a nova senha usando BCrypt.
- Não cria migration, pois utiliza a coluna `senha_hash` existente.
- Não implementa recuperação por e-mail nem redefinição administrativa nesta etapa.

## Executar
Na raiz do projeto:
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
Depois de entrar no sistema, acesse `/minha-conta/senha`.

A compilação precisa ser confirmada no ambiente local.
