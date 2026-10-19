#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

: "${BACKUP_DIR:=/backups}"
: "${DB_NAME:?Defina DB_NAME}"
: "${S3_BUCKET:?Defina S3_BUCKET com o bucket de destino}"
: "${AWS_ACCESS_KEY_ID:?Defina AWS_ACCESS_KEY_ID no ambiente do job}"
: "${AWS_SECRET_ACCESS_KEY:?Defina AWS_SECRET_ACCESS_KEY no ambiente do job}"
: "${AWS_DEFAULT_REGION:=us-east-1}"
: "${S3_PREFIX:=centro-cirurgico/postgres}"

if ! command -v aws >/dev/null 2>&1; then
  echo "AWS CLI não está instalado." >&2
  exit 2
fi

# Endpoint opcional para provedores compatíveis com S3.
endpoint_args=()
if [[ -n "${S3_ENDPOINT_URL:-}" ]]; then
  endpoint_args+=(--endpoint-url "$S3_ENDPOINT_URL")
fi

latest_dump=$(find "$BACKUP_DIR" -maxdepth 1 -type f -name "${DB_NAME}_*.dump" -printf '%T@ %p\n' | sort -nr | head -n1 | cut -d' ' -f2-)
if [[ -z "$latest_dump" || ! -s "$latest_dump" ]]; then
  echo "Nenhum backup válido encontrado em $BACKUP_DIR para o banco $DB_NAME." >&2
  exit 1
fi
checksum_file="${latest_dump}.sha256"
if [[ ! -s "$checksum_file" ]]; then
  echo "Arquivo de checksum ausente: $checksum_file" >&2
  exit 1
fi

(cd "$(dirname "$latest_dump")" && sha256sum -c "$(basename "$checksum_file")")
object_prefix="${S3_PREFIX#/}"
object_prefix="${object_prefix%/}"
base_name=$(basename "$latest_dump")
if [[ -n "$object_prefix" ]]; then
  remote_base="s3://${S3_BUCKET}/${object_prefix}/${base_name}"
else
  remote_base="s3://${S3_BUCKET}/${base_name}"
fi

aws s3 cp "$latest_dump" "$remote_base" "${endpoint_args[@]}" --only-show-errors
aws s3 cp "$checksum_file" "${remote_base}.sha256" "${endpoint_args[@]}" --only-show-errors

local_size=$(wc -c < "$latest_dump" | tr -d ' ')
remote_size=$(aws s3api head-object --bucket "$S3_BUCKET" --key "${object_prefix:+$object_prefix/}${base_name}" "${endpoint_args[@]}" --query ContentLength --output text)
if [[ "$local_size" != "$remote_size" ]]; then
  echo "Falha na verificação remota: tamanho local=$local_size remoto=$remote_size." >&2
  exit 1
fi

echo "Backup enviado e verificado por tamanho: $remote_base (bytes=$local_size)."
echo "Checksum SHA-256 enviado separadamente: ${remote_base}.sha256"
