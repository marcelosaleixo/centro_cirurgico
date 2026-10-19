#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

# Ensaio destrutivo APENAS no banco de teste informado explicitamente.
: "${BACKUP_DIR:=/backups}"
: "${DB_NAME:?Defina DB_NAME como o banco de origem dos backups}"
: "${DB_HOST:?Defina DB_HOST}"
: "${DB_PORT:=5432}"
: "${DB_USERNAME:?Defina DB_USERNAME}"
: "${DB_PASSWORD:?Defina DB_PASSWORD}"
: "${DRILL_DB_NAME:?Defina DRILL_DB_NAME para um banco exclusivo de teste}"

if [[ "$DRILL_DB_NAME" == "$DB_NAME" ]]; then
  echo "ERRO: o banco de teste não pode ser igual ao banco de origem." >&2
  exit 2
fi
if [[ "$DRILL_DB_NAME" != *restore_test ]]; then
  echo "ERRO: por segurança, DRILL_DB_NAME deve terminar com restore_test (ex.: centro_restore_test)." >&2
  exit 2
fi
if [[ "${DRILL_CONFIRM:-}" != "SIM_RESTAURAR_BANCO_DE_TESTE" ]]; then
  echo "ERRO: defina DRILL_CONFIRM=SIM_RESTAURAR_BANCO_DE_TESTE após confirmar o destino." >&2
  exit 2
fi

shopt -s nullglob
files=("$BACKUP_DIR"/"${DB_NAME}"_*.dump)
if (( ${#files[@]} == 0 )); then
  echo "ERRO: nenhum backup encontrado em $BACKUP_DIR para $DB_NAME." >&2
  exit 1
fi
latest=""
latest_epoch=0
for f in "${files[@]}"; do
  [[ -s "$f" ]] || continue
  epoch=$(stat -c %Y "$f")
  if (( epoch > latest_epoch )); then latest_epoch=$epoch; latest="$f"; fi
done
[[ -n "$latest" ]] || { echo "ERRO: nenhum dump válido encontrado." >&2; exit 1; }
checksum="${latest}.sha256"
[[ -s "$checksum" ]] || { echo "ERRO: checksum ausente: $checksum" >&2; exit 1; }
(cd "$(dirname "$latest")" && sha256sum -c "$(basename "$checksum")")
pg_restore --list "$latest" >/dev/null

export PGPASSWORD="$DB_PASSWORD"
trap 'unset PGPASSWORD' EXIT
# A opção --clean remove objetos no banco de teste. Nunca aponte para produção.
echo "Ensaio de restauração iniciado. Origem do dump: $latest"
echo "Destino EXCLUSIVO de teste: ${DB_HOST}:${DB_PORT}/${DRILL_DB_NAME}"
pg_restore \
  --host="$DB_HOST" \
  --port="$DB_PORT" \
  --username="$DB_USERNAME" \
  --dbname="$DRILL_DB_NAME" \
  --clean --if-exists --no-owner --no-privileges \
  --exit-on-error \
  "$latest"

# Confirma que o destino responde e contém pelo menos uma tabela de usuário.
table_count=$(psql \
  --host="$DB_HOST" --port="$DB_PORT" --username="$DB_USERNAME" \
  --dbname="$DRILL_DB_NAME" --no-psqlrc --tuples-only --no-align \
  --command="SELECT count(*) FROM information_schema.tables WHERE table_schema NOT IN ('pg_catalog','information_schema') AND table_type='BASE TABLE';")
if ! [[ "$table_count" =~ ^[0-9]+$ ]] || (( table_count < 1 )); then
  echo "ERRO: restauração terminou, mas nenhuma tabela de usuário foi encontrada." >&2
  exit 1
fi
echo "SUCESSO: restauração de teste concluída. Tabelas encontradas: $table_count."
echo "Este resultado não substitui a validação funcional da aplicação restaurada."
