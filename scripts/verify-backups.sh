#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

: "${BACKUP_DIR:=/backups}"
: "${DB_NAME:?Defina DB_NAME para validar os arquivos deste banco}"
: "${MAX_BACKUP_AGE_HOURS:=36}"

if ! [[ "$MAX_BACKUP_AGE_HOURS" =~ ^[0-9]+$ ]] || [[ "$MAX_BACKUP_AGE_HOURS" -lt 1 ]]; then
  echo "ERRO: MAX_BACKUP_AGE_HOURS deve ser um inteiro maior que zero." >&2
  exit 2
fi

if [[ ! -d "$BACKUP_DIR" ]]; then
  echo "ERRO: diretório de backup não encontrado: $BACKUP_DIR" >&2
  exit 1
fi

shopt -s nullglob
files=("$BACKUP_DIR"/"${DB_NAME}"_*.dump)
if (( ${#files[@]} == 0 )); then
  echo "ERRO: nenhum dump encontrado para o banco ${DB_NAME} em ${BACKUP_DIR}." >&2
  exit 1
fi

now_epoch=$(date +%s)
verified=0
latest_epoch=0
latest_file=""
for dump in "${files[@]}"; do
  [[ -s "$dump" ]] || { echo "ERRO: arquivo vazio: $dump" >&2; exit 1; }
  checksum="${dump}.sha256"
  [[ -s "$checksum" ]] || { echo "ERRO: checksum ausente: $checksum" >&2; exit 1; }
  (cd "$(dirname "$dump")" && sha256sum -c "$(basename "$checksum")")
  # pg_restore --list valida se o arquivo custom contém um catálogo legível.
  pg_restore --list "$dump" >/dev/null
  modified_epoch=$(stat -c %Y "$dump")
  (( modified_epoch > latest_epoch )) && { latest_epoch=$modified_epoch; latest_file=$dump; }
  verified=$((verified + 1))
done

age_seconds=$((now_epoch - latest_epoch))
max_age_seconds=$((MAX_BACKUP_AGE_HOURS * 3600))
if (( age_seconds > max_age_seconds )); then
  age_hours=$((age_seconds / 3600))
  echo "ERRO: backup mais recente está velho (${age_hours}h); limite=${MAX_BACKUP_AGE_HOURS}h. Arquivo: ${latest_file}" >&2
  exit 1
fi

echo "OK: ${verified} backup(s) validado(s); mais recente: ${latest_file}; idade aproximada: $((age_seconds / 3600))h."
