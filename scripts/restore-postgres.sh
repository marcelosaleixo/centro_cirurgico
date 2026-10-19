#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

if [[ $# -ne 1 ]]; then
  echo "Uso: $0 caminho/arquivo.dump" >&2
  exit 2
fi

dump_file="$1"
[[ -f "$dump_file" ]] || { echo "Arquivo não encontrado: $dump_file" >&2; exit 2; }

: "${DB_HOST:?Defina DB_HOST}"
: "${DB_PORT:=5432}"
: "${DB_NAME:?Defina DB_NAME}"
: "${DB_USERNAME:?Defina DB_USERNAME}"
export PGPASSWORD="${DB_PASSWORD:?Defina DB_PASSWORD}"

echo "ATENÇÃO: a restauração pode sobrescrever objetos no banco de destino."
echo "Destino: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
read -r -p "Digite RESTAURAR para confirmar: " confirmation
[[ "$confirmation" == "RESTAURAR" ]] || { echo "Cancelado."; exit 1; }

pg_restore \
  --host="$DB_HOST" \
  --port="$DB_PORT" \
  --username="$DB_USERNAME" \
  --dbname="$DB_NAME" \
  --clean \
  --if-exists \
  --no-owner \
  --no-privileges \
  "$dump_file"

unset PGPASSWORD
echo "Restauração concluída. Valide a aplicação antes de liberar o uso."
