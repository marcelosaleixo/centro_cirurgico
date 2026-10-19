#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

: "${DB_HOST:?Defina DB_HOST com o hostname interno do PostgreSQL}"
: "${DB_PORT:=5432}"
: "${DB_NAME:?Defina DB_NAME com o nome do banco}"
: "${DB_USERNAME:?Defina DB_USERNAME com o usuário do banco}"
: "${BACKUP_DIR:=./backups}"

mkdir -p "$BACKUP_DIR"
timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
output="${BACKUP_DIR%/}/${DB_NAME}_${timestamp}.dump"
export PGPASSWORD="${DB_PASSWORD:?Defina DB_PASSWORD no ambiente}"

echo "Iniciando backup PostgreSQL: ${DB_NAME} (${timestamp})"
pg_dump \
  --host="$DB_HOST" \
  --port="$DB_PORT" \
  --username="$DB_USERNAME" \
  --dbname="$DB_NAME" \
  --format=custom \
  --no-owner \
  --no-privileges \
  --file="$output"

unset PGPASSWORD
test -s "$output"
sha256sum "$output" > "${output}.sha256"
echo "Backup concluído: $output"
echo "Checksum: ${output}.sha256"
