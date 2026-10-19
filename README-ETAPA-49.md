# Etapa 49 — Painel de monitoramento de backups

## O que mudou
- Nova tela administrativa em `/admin/backups`.
- Mostra o último backup local, o último envio `upload-s3`, a última validação, as últimas 30 operações e a contagem de falhas nos últimos 100 registros.
- Acesso restrito ao perfil `ADMIN_RH`.
- Leitura dos arquivos de status e histórico produzidos pelos scripts da Etapa 47/48.
- Alerta visual se não houver backup local com idade menor que o limite configurado.

## Configuração obrigatória no EasyPanel
O painel web precisa ter acesso de leitura ao mesmo volume persistente `/backups` usado pelos jobs. Monte o volume em `/backups` no serviço da aplicação, preferencialmente como somente leitura se o EasyPanel permitir.

Variáveis opcionais:
- `BACKUP_STATUS_FILE=/backups/backup-last-status.txt`
- `BACKUP_HISTORY_FILE=/backups/backup-operations.csv`
- `BACKUP_MAX_AGE_HOURS=36`

O script `run-and-record.sh` deve continuar registrando as ações `backup`, `verify`, `restore-drill` e `upload-s3` no mesmo arquivo de histórico. Se cada job usa um volume distinto, o painel não verá os registros.

## Implantação
1. Faça backup do código e atualize o repositório com os arquivos desta etapa.
2. Monte o volume persistente compartilhado `/backups` na aplicação web com acesso somente leitura, se possível.
3. Configure as variáveis acima e reconstrua/reimplante com o Dockerfile Java 21 existente.
4. Entre com um usuário `ADMIN_RH` e abra **Administração → Monitoramento de backups** ou `/admin/backups`.
5. Execute um backup, uma validação e um envio S3 pelos jobs; atualize a tela para confirmar o histórico.

## Limitações
- O painel não inicia nem agenda tarefas; apenas lê os registros dos scripts.
- O último envio S3 indica o resultado registrado pelo job, não uma restauração remota comprovada.
- O indicador de falhas considera até os últimos 100 registros do histórico disponível.
- Não exponha o CSV nem o arquivo de status publicamente. Conceda à aplicação apenas leitura.
- Não foi possível executar build Maven/Docker nem validar contra um ambiente PostgreSQL/EasyPanel real neste ambiente.
