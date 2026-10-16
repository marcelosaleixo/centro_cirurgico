# Etapa 30 — Permissões por funcionalidade

## Objetivo
Centralizar as regras de autorização por rota usando Spring Security, preservando os perfis já existentes: `ADMIN_RH`, `RH` e `CONSULTA`.

## Matriz de acesso

| Funcionalidade | ADMIN_RH | RH | CONSULTA |
|---|---:|---:|---:|
| Usuários, auditoria e painel de segurança | Sim | Não | Não |
| Consultar módulos operacionais | Sim | Sim | Sim |
| Cadastrar/alterar dados operacionais via POST | Sim | Sim | Não |
| Conferir fechamento mensal de RH | Sim | Sim | Não |
| Fechar ou reabrir período de RH | Sim | Não | Não |
| Alterar a própria senha | Sim | Sim | Sim |

## Arquivo principal
As regras ficam em `security/SegurancaConfig.java`. As regras específicas aparecem antes da regra geral para que uma autorização ampla não sobreponha as restrições administrativas.

## Teste local
1. Execute `./mvnw clean test` (Linux/macOS) ou `.\\mvnw.cmd clean test` (Windows).
2. Entre com cada perfil de teste.
3. Confirme que `CONSULTA` consegue abrir páginas de listagem, mas recebe acesso negado ao enviar formulários de alteração.
4. Confirme que `RH` não acessa `/admin/usuarios`, `/admin/auditoria-seguranca` nem `/admin/seguranca`.
5. Confirme que somente `ADMIN_RH` consegue fechar ou reabrir o período de RH.

## Observações
- A proteção CSRF continua habilitada.
- Esta etapa aplica autorização no servidor; esconder botões no HTML, por si só, não substitui essas regras.
- Se forem adicionados novos endpoints, atualize a matriz e as regras de autorização antes de publicar.
