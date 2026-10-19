# Etapa 44 — Backup automatizado e retenção

## Objetivo
Preparar uma rotina diária de backup PostgreSQL com retenção de arquivos antigos. A execução continua sendo configurada pelo administrador no EasyPanel/cron; não é executada automaticamente pelo serviço web.

## Componentes
- `scripts/backup-with-retention.sh`: executa o backup da Etapa 43 e remove apenas arquivos no padrão `${DB_NAME}_*.dump` e seus checksums após o prazo configurado.
- `scripts/Dockerfile.backup`: imagem isolada baseada em `postgres:16-bookworm` para rodar o job.
- `scripts/cron-backup.example`: exemplo de cron diário às 02:30.
- `BACKUP_RETENTION_DAYS`: retenção em dias, padrão 14.
- `BACKUP_DIR`: diretório de saída, recomendado como volume persistente `/backups`.

## Implantação recomendada no EasyPanel
1. Crie um serviço de tarefas separado do serviço web (não execute cron no contêiner da aplicação).
2. Configure o build usando `scripts/Dockerfile.backup` como Dockerfile do job, com contexto de build na raiz do repositório. Se a interface não permitir Dockerfile fora da raiz, copie esse Dockerfile para a raiz em uma configuração de serviço dedicada ou crie um diretório de job separado.
3. Configure `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `BACKUP_DIR=/backups` e `BACKUP_RETENTION_DAYS=14` nas variáveis do job. Não copie as senhas para Git.
4. Monte `/backups` como volume persistente; sem volume, os arquivos podem desaparecer quando o contêiner for recriado.
5. Configure o agendador do EasyPanel/cron para iniciar o job diariamente, idealmente em horário de menor uso, como 02:30 no fuso correto. Confirme que o mecanismo de tarefas do seu plano permite agendamento e ajuste a expressão conforme a interface.
6. Armazene também uma cópia externa criptografada; volume no mesmo host não protege contra perda do host.
7. Execute uma vez manualmente e confirme `.dump` e `.sha256` no volume antes de ativar o agendamento.

## Segurança e limites
- O script usa `pg_dump` e não altera o banco de origem.
- A retenção apaga somente arquivos que correspondem ao padrão do banco configurado e excedem o número de dias definido.
- Proteja o volume e as variáveis de ambiente; dumps podem conter dados sensíveis.
- O exemplo cron é ilustrativo; não é aplicado automaticamente no EasyPanel.
- Não configure o backup dentro do contêiner web do Spring Boot.
- Não existe nesta etapa envio para S3/Backblaze/Google Cloud nem alerta por e-mail.

## Validação
ZIP verificado estruturalmente. Não foi possível executar backup real, cron, build Docker ou restauração neste ambiente.
