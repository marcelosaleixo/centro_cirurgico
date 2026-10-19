# Etapa 47 — Histórico e relatório operacional de backups

## Objetivo
Registrar as execuções de backup, verificação e ensaio de restauração em um histórico CSV no volume persistente, além de manter um resumo da última execução. O relatório ajuda a equipe a identificar falhas e tempos de execução sem registrar senhas.

## Novos arquivos
- `scripts/run-and-record.sh`: executa uma rotina (`backup`, `verify` ou `restore-drill`), registra status, código de saída, duração, horário UTC, host e nome do banco.
- `scripts/backup-status-report.sh`: apresenta as últimas operações e a quantidade de falhas encontradas no histórico disponível.
- `scripts/Dockerfile.backup`: inclui os novos scripts na imagem do job.

## Execução
O volume persistente deve estar montado em `/backups`. Para registrar uma operação, sobrescreva o comando do job no EasyPanel, mantendo a imagem construída com `scripts/Dockerfile.backup`:

```bash
/opt/backup/run-and-record.sh backup
/opt/backup/run-and-record.sh verify
/opt/backup/run-and-record.sh restore-drill
```

O ensaio `restore-drill` continua exigindo `DRILL_DB_NAME` terminado em `restore_test` e `DRILL_CONFIRM=SIM_RESTAURAR_BANCO_DE_TESTE`. Não use o banco de produção como destino.

Para consultar o histórico manualmente, use o comando:

```bash
/opt/backup/backup-status-report.sh
```

Variáveis opcionais:
- `BACKUP_STATUS_FILE`: caminho alternativo do CSV; padrão `/backups/backup-operations.csv`.
- `REPORT_LIMIT`: quantidade de registros exibidos; padrão 20.

Os arquivos gerados são `backup-operations.csv` e `backup-last-status.txt` dentro de `/backups`. Garanta que o volume seja persistente e tenha acesso restrito, pois os nomes de host e banco são dados operacionais. Senhas não são gravadas no histórico.

## Configuração no EasyPanel
1. Reconstrua a imagem do job com o `scripts/Dockerfile.backup` atualizado.
2. Confirme que os jobs de backup, verificação e ensaio montam o mesmo volume persistente em `/backups`.
3. Altere o comando de cada job para o wrapper `run-and-record.sh` com a ação apropriada.
4. Execute uma vez manualmente e consulte o relatório.
5. Configure o agendador para tratar código de saída diferente de zero como falha e habilite notificações se a plataforma oferecer esse recurso.

**Importante:** o CSV é histórico local no volume, não um serviço de monitoramento central e não envia notificações por conta própria. Se o job falhar abruptamente antes de gravar o registro final (por exemplo, encerramento forçado do container), a execução pode não aparecer no CSV. Faça cópia externa protegida do volume/histórico.

## Validação desta entrega
ZIP verificado estruturalmente. Os scripts foram revisados, mas não foram executados contra PostgreSQL real nem foi construída a imagem Docker neste ambiente.
