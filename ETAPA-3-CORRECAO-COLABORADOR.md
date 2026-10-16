# Etapa 3 — Correção de Colaborador.java

## Correção
A entidade `Colaborador` agora possui:
- `isAtivo()` — getter padrão JavaBeans para o atributo boolean `ativo`, usado pelos serviços de escala, férias e troca de plantão.
- `getAtivo()` — mantido para compatibilidade com qualquer código existente.
- `setAtivo(boolean ativo)` — setter do atributo.

## Aplicação
Na raiz do projeto, execute no PowerShell:
```powershell
Remove-Item -Recurse -Force .\target -ErrorAction SilentlyContinue
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

Essa alteração é somente no código Java e não exige apagar nem recriar o banco de dados.
