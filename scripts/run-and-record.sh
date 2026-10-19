#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

: "${BACKUP_DIR:=/backups}"
ACTION="${1:-backup}"
case "$ACTION" in backup|verify|restore-drill|upload-s3) ;; report) exec "$(dirname "$0")/backup-status-report.sh" ;; *) echo "Uso: $0 {backup|verify|restore-drill|upload-s3|report}" >&2; exit 2 ;; esac
mkdir -p "$BACKUP_DIR"
HISTORY_FILE="${BACKUP_STATUS_FILE:-${BACKUP_DIR%/}/backup-operations.csv}"
mkdir -p "$(dirname "$HISTORY_FILE")"
if [[ ! -e "$HISTORY_FILE" ]]; then
  printf 'started_utc,finished_utc,action,status,exit_code,duration_seconds,host,db_name,message\n' > "$HISTORY_FILE"
fi
start_epoch=$(date +%s)
started=$(date -u +%Y-%m-%dT%H:%M:%SZ)
host=${DB_HOST:-unknown}; db=${DB_NAME:-unknown}
# CSV escaping helper: quote every field and double embedded quotes.
csvq() { local s=${1-}; s=${s//\"/\"\"}; printf '"%s"' "$s"; }
set +e
case "$ACTION" in
  backup) "$(dirname "$0")/backup-with-retention.sh" ;;
  verify) "$(dirname "$0")/verify-backups.sh" ;;
  restore-drill) "$(dirname "$0")/restore-drill.sh" ;;
  upload-s3) "$(dirname "$0")/upload-backup-s3.sh" ;;
esac
rc=$?
set -e
end_epoch=$(date +%s); finished=$(date -u +%Y-%m-%dT%H:%M:%SZ); duration=$((end_epoch-start_epoch))
if (( rc == 0 )); then status=SUCCESS; message="Execucao concluida"; else status=FAILURE; message="Execucao terminou com erro"; fi
{
  csvq "$started"; printf ','; csvq "$finished"; printf ','; csvq "$ACTION"; printf ','; csvq "$status"; printf ','; csvq "$rc"; printf ','; csvq "$duration"; printf ','; csvq "$host"; printf ','; csvq "$db"; printf ','; csvq "$message"; printf '\n'
} >> "$HISTORY_FILE"
# A visão mais recente é mantida separadamente para facilitar health checks e suporte.
printf 'checked_at_utc=%s\naction=%s\nstatus=%s\nexit_code=%s\nduration_seconds=%s\nhost=%s\ndb_name=%s\n' \
  "$finished" "$ACTION" "$status" "$rc" "$duration" "$host" "$db" > "${BACKUP_DIR%/}/backup-last-status.txt"
echo "Operacao registrada: action=$ACTION status=$status duration=${duration}s history=$HISTORY_FILE"
# Por padrão, notifica falhas. Para receber sucesso também, use WEBHOOK_NOTIFY_SUCCESS=true.
notify=false
if [[ "$status" == "FAILURE" || "${WEBHOOK_NOTIFY_SUCCESS:-false}" == "true" ]]; then notify=true; fi
if [[ "$notify" == "true" && -n "${WEBHOOK_URL:-}" ]]; then
  if ! "$(dirname "$0")/notify-webhook.sh" "$status" "$ACTION" "$finished" "$rc" "$duration" "$host" "$db" "$message"; then
    echo "AVISO: operação registrada, mas a notificação não foi entregue." >&2
    # Não mascara o resultado original da rotina de backup/validação.
  fi
fi
exit "$rc"
