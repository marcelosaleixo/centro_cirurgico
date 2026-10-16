# Etapa 26 — Bloqueio temporário após tentativas inválidas

## Regras
- A partir de 5 tentativas de login inválidas para uma conta existente, a conta fica bloqueada por 15 minutos.
- Tentativas durante o bloqueio não estendem o período.
- Após expirar o bloqueio, a próxima falha inicia nova contagem.
- Um login bem-sucedido zera a contagem e remove eventual bloqueio.
- O histórico registra falhas, bloqueios e logins bem-sucedidos.
- Não registra senhas; a mensagem de login é genérica para não revelar se a conta existe.

## Banco de dados
A migration Flyway V12 adiciona `tentativas_falhas` e `bloqueado_ate` à tabela `usuarios_sistema`.

## Execução
Faça backup do banco. Na raiz do projeto:
```powershell
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```
O Flyway deverá aplicar V12 automaticamente. Teste em ambiente de desenvolvimento antes de produção.

## Observações
- Bloqueio por conta não substitui rate limiting no nível da aplicação/proxy; para produção, combine com limites por IP e monitoramento.
- A compilação precisa ser confirmada no ambiente local.
