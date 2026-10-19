# Etapa 51 — Monitoramento de disponibilidade da aplicação

## Objetivo

Adicionar um job independente que consulte o endpoint de saúde do Spring Boot Actuator, registre o estado atual em arquivo persistente e envie um webhook apenas quando houver transição entre `UP` e `DOWN`.

## Arquivos

- `scripts/monitor-application-health.sh`: executa a verificação HTTP e valida o JSON do Actuator.
- `scripts/Dockerfile.backup`: inclui o monitor na imagem usada pelos jobs.

## Variáveis do job no EasyPanel

```env
HEALTHCHECK_URL=https://SEU-DOMINIO/actuator/health
HEALTH_STATE_FILE=/backups/application-health-state
WEBHOOK_URL=https://SEU-ENDPOINT-DE-WEBHOOK
HEALTH_CONNECT_TIMEOUT_SECONDS=5
HEALTH_TIMEOUT_SECONDS=10
WEBHOOK_CONNECT_TIMEOUT_SECONDS=5
WEBHOOK_TIMEOUT_SECONDS=15
```

Use o URL público com HTTPS ou um endereço interno acessível a partir do job. O endpoint configurado deve responder com JSON contendo `"status":"UP"` quando a aplicação estiver saudável. Não inclua tokens nem senhas na URL. Se o endpoint estiver protegido por autenticação, configure um endpoint de monitoramento apropriado em vez de expor informações sensíveis.

## Configuração

1. Faça deploy da aplicação com Actuator habilitado e confira manualmente `/actuator/health`.
2. Reconstrua a imagem do job usando `scripts/Dockerfile.backup`.
3. Crie um job agendado separado, por exemplo a cada 5 minutos, e configure as variáveis acima.
4. Monte um volume persistente em `/backups` para manter `application-health-state` entre execuções.
5. Execute o comando do job:

```bash
/opt/backup/monitor-application-health.sh
```

6. Teste os dois cenários: aplicação disponível (`UP`) e aplicação indisponível (`DOWN`). O webhook é enviado apenas quando o estado muda. O script retorna código zero quando `UP` e não zero quando `DOWN`, para o agendador registrar a falha.

## Comportamento e limites

- O monitor não reinicia a aplicação; apenas verifica, registra e notifica.
- Na primeira execução, o estado anterior é `UNKNOWN`, então envia notificação se houver webhook configurado.
- Se o webhook falhar, o estado local continua registrado e a execução continua refletindo o estado da aplicação.
- O arquivo de estado deve estar em volume persistente compartilhado entre execuções do job. Não use armazenamento efêmero.
- Para evitar notificações falsas, use um intervalo adequado e confirme o comportamento do Actuator na versão implantada.
- Não foi executado teste contra um servidor real nem build Docker neste ambiente.
