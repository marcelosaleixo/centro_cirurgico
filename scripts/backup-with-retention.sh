#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

: "${BACKUP_DIR:=/backups}"
: "${BACKUP_RETENTION_DAYS:=14}"
: "${DB_HOST:?Defina DB_HOST}"
: "${DB_PORT:=5432}"
: "${DB_NAME:?Defina DB_NAME}"
: "${DB_USERNAME:?Defina DB_USERNAME}"
: "${DB_PASSWORD:?Defina DB_PASSWORD}"

mkdir -p "$BACKUP_DIR"
export DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD BACKUP_DIR

"$(dirname "$0")/backup-postgres.sh"

# Apaga apenas backups gerados pelo padrão deste script, com idade superior à retenção.
find "$BACKUP_DIR" -maxdepth 1 -type f   \( -name "${DB_NAME}_*.dump" -o -name "${DB_NAME}_*.dump.sha256" \)   -mtime "+$BACKUP_RETENTION_DAYS" -print -delete

echo "Rotina finalizada. Retenção configurada: ${BACKUP_RETENTION_DAYS} dias."
