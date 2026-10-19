# Etapa 50 — Notificações de falhas e backups atrasados

## O que mudou
- `scripts/notify-webhook.sh` envia eventos operacionais em JSON para um endpoint HTTP configurado.
- `scripts/run-and-record.sh` notifica automaticamente falhas nas ações `backup`, `verify`, `restore-drill` e `upload-s3`.
- Notificações de sucesso são opcionais via `WEBHOOK_NOTIFY_SUCCESS=true`.
- Falha no webhook é registrada como aviso, sem mascarar o resultado original do backup.
- A imagem `scripts/Dockerfile.backup` instala `curl` e inclui o novo script.

## Configuração no EasyPanel
Defina no serviço/job de backup:

```env
WEBHOOK_URL=https://seu-endpoint-de-webhook
WEBHOOK_CONNECT_TIMEOUT_SECONDS=5
WEBHOOK_TIMEOUT_SECONDS=15
WEBHOOK_NOTIFY_SUCCESS=false
```

Use o URL real fornecido pelo seu serviço de notificações e guarde-o como variável secreta. Não coloque tokens no repositório. O endpoint precisa aceitar `POST application/json` e retornar HTTP 2xx. O payload contém evento, status, ação, horário UTC, código de saída, duração, host, nome do banco e mensagem — não contém senhas nem dados do backup.

O script é compatível com endpoints genéricos que aceitem o payload JSON definido. Slack, Discord, Teams e outros provedores podem exigir um formato específico ou um adaptador próprio; não presuma compatibilidade direta sem testar.

## Comandos

```bash
/opt/backup/run-and-record.sh backup
/opt/backup/run-and-record.sh verify
/opt/backup/run-and-record.sh upload-s3
```

Para notificar também cada execução bem-sucedida, defina `WEBHOOK_NOTIFY_SUCCESS=true`. Recomenda-se deixar `false` para evitar excesso de mensagens e receber apenas falhas.

## Backups atrasados
O job de validação já detecta ausência de backup ou backup mais antigo que `MAX_BACKUP_AGE_HOURS` (36 horas por padrão). Agende `verify` periodicamente; quando falhar, o wrapper registra o resultado e envia a notificação. Para alertas de atraso, portanto, o job de validação precisa estar agendado e usar o mesmo volume persistente `/backups`.

## Implantação
1. Atualize o projeto e reconstrua a imagem usando `scripts/Dockerfile.backup`.
2. Configure `WEBHOOK_URL` como secret no EasyPanel.
3. Garanta que os jobs usem `scripts/run-and-record.sh` em vez de executar diretamente os scripts de backup, para que histórico e alertas sejam registrados.
4. Execute um teste controlado em ambiente não produtivo e confirme o recebimento do webhook.
5. Configure o job `verify` para rodar após o backup e periodicamente, com `MAX_BACKUP_AGE_HOURS=36`.

## Limitações e segurança
- Nenhuma notificação real foi enviada durante a preparação desta etapa.
- Não foi executado build Docker nem teste contra provedor de webhook real.
- Falhas de entrega geram aviso no log, mas não alteram o código de saída da operação de backup; configure monitoramento do próprio job para detectar isso.
- Restrinja acesso ao URL do webhook e não imprima seu valor em logs.
