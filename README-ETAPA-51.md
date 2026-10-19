# Etapa 51 — Monitoramento de disponibilidade

Continuação da Etapa 50. Adiciona `scripts/monitor-application-health.sh`, um job de monitoramento do Spring Boot Actuator que registra mudanças de estado e notifica via webhook quando o estado muda entre `UP` e `DOWN`.

Leia `ETAPA-51-MONITORAMENTO-DISPONIBILIDADE.md` para configurar o job agendado no EasyPanel.

O monitor não reinicia a aplicação e não configura o agendamento sozinho. ZIP e sintaxe Bash foram verificados; não foi feito teste real HTTP, envio de webhook ou build Docker.
