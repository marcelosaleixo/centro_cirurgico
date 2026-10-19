# Etapa 45 — Validação de integridade dos backups

## Objetivo
Detectar cedo arquivos de backup ausentes, vazios, com checksum divergente, ilegíveis pelo `pg_restore` ou cuja cópia mais recente esteja antiga demais. A validação não restaura dados nem altera o banco de origem.

## Arquivos
- `scripts/verify-backups.sh`: verifica os dumps encontrados para `DB_NAME` em `BACKUP_DIR`.
- `scripts/Dockerfile.backup`: inclui o verificador na imagem isolada do job.
- `MAX_BACKUP_AGE_HOURS`: idade máxima permitida para o backup mais recente; padrão recomendado de 36 horas.

## Execução manual
Com PostgreSQL client instalado e volume de backups montado:

```bash
BACKUP_DIR=/backups DB_NAME=nome_do_banco MAX_BACKUP_AGE_HOURS=36 \
  ./scripts/verify-backups.sh
```

O script precisa de `sha256sum`, `pg_restore`, `stat` e Bash. Ele verifica todos os arquivos `${DB_NAME}_*.dump` que encontrar e exige um `.dump.sha256` correspondente. Se algum arquivo falhar, ou se o backup mais recente exceder a idade máxima, retorna código diferente de zero.

## EasyPanel
1. Reutilize a imagem construída com `scripts/Dockerfile.backup`, com o mesmo volume persistente montado em `/backups`.
2. Crie um job agendado separado para validação, usando o comando `/opt/backup/verify-backups.sh` em vez do entrypoint padrão de backup. Configure `BACKUP_DIR=/backups`, `DB_NAME` e `MAX_BACKUP_AGE_HOURS=36`.
3. Agende a validação depois do horário de backup, por exemplo diariamente às 03:00 se o backup estiver marcado para 02:30. Ajuste ao fuso horário configurado no EasyPanel.
4. Configure o sistema de tarefas para tratar código de saída diferente de zero como falha e habilite notificações de falha, se disponíveis.
5. Faça um teste proposital em ambiente controlado (por exemplo, cópia de um dump com checksum alterado) para confirmar que o job identifica falhas; não altere os backups reais.

## Importante
- A verificação de checksum detecta corrupção em relação ao checksum salvo; não comprova que o conteúdo é recuperável em todos os cenários. `pg_restore --list` testa se o catálogo do dump pode ser lido, mas não substitui um teste de restauração em banco separado.
- Mantenha cópia externa protegida. Um volume no mesmo host não protege contra perda do host.
- Não há envio de e-mail integrado nesta etapa. As notificações dependem do agendador/plataforma configurados.
- Esta rotina não fica agendada automaticamente ao publicar o sistema. É necessário criar o job no EasyPanel.

## Validação desta entrega
O pacote foi verificado estruturalmente. Não foi possível executar `pg_restore`, testar contra um PostgreSQL real, construir a imagem Docker ou configurar o job no EasyPanel neste ambiente.
