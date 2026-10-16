# Etapa 21 — Reabertura controlada do fechamento do RH

## Implementação
- Adiciona a ação POST `/relatorios/fechamento-rh/reabrir`.
- Exige justificativa com no mínimo 10 caracteres e limita o texto a 1000 caracteres.
- Só permite reabrir um período que exista e esteja FECHADO.
- Altera o status para ABERTO e limpa `fechado_em`, conforme a constraint da migration V8.
- Registra `PERIODO_REABERTO` no histórico de auditoria com status anterior/novo e justificativa.
- Exibe formulário de reabertura na tela de fechamento quando o período está fechado.

## Instalação
Não foi adicionada migration: a estrutura atual já comporta a reabertura e a auditoria por meio das tabelas criadas nas etapas anteriores.

1. Faça backup do banco e substitua os arquivos pela versão desta etapa.
2. Execute `./mvnw clean package -DskipTests` (Windows: `.\mvnw.cmd clean package -DskipTests`).
3. Execute `./mvnw spring-boot:run` (Windows: ` .\mvnw.cmd spring-boot:run`).
4. Abra `/relatorios/fechamento-rh`, consulte um período fechado e informe a justificativa para reabri-lo.

## Segurança e validação
A implementação valida o estado do período e a justificativa, mas não inventa integração com papéis de segurança que não está configurada no projeto. Antes de disponibilizar em produção, proteja a rota de reabertura com autorização exclusiva para o RH/administrador. O responsável de auditoria continua identificado como `sistema` na versão atual.

A compilação não foi validada neste ambiente; confirme-a no ambiente local.
