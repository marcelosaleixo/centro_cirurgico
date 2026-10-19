#!/usr/bin/env bash
set -Eeuo pipefail

# Envia um evento operacional para um webhook HTTP que aceite JSON.
# A notificação não deve conter credenciais nem conteúdo do backup.
: "${WEBHOOK_URL:=}"
if [[ -z "$WEBHOOK_URL" ]]; then
  echo "WEBHOOK_URL não configurado; notificação ignorada."
  exit 0
fi
command -v curl >/dev/null 2>&1 || { echo "ERRO: curl não está instalado." >&2; exit 1; }

json_escape() {
  local s=${1-}
  s=${s//\\/\\\\}
  s=${s//\"/\\\"}
  s=${s//$'\n'/\\n}
  s=${s//$'\r'/\\r}
  s=${s//$'\t'/\\t}
  printf '%s' "$s"
}

status="${1:-UNKNOWN}"
action="${2:-unknown}"
finished="${3:-$(date -u +%Y-%m-%dT%H:%M:%SZ)}"
exit_code="${4:-1}"
duration="${5:-0}"
host="${6:-unknown}"
db="${7:-unknown}"
message="${8:-Sem detalhes}"

payload=$(printf '{"event":"centro-cirurgico.backup","status":"%s","action":"%s","timestamp_utc":"%s","exit_code":%s,"duration_seconds":%s,"host":"%s","db_name":"%s","message":"%s"}' \
  "$(json_escape "$status")" "$(json_escape "$action")" "$(json_escape "$finished")" \
  "$exit_code" "$duration" "$(json_escape "$host")" "$(json_escape "$db")" "$(json_escape "$message")")

http_code=$(curl --silent --show-error --output /tmp/backup-webhook-response.txt \
  --write-out '%{http_code}' --connect-timeout "${WEBHOOK_CONNECT_TIMEOUT_SECONDS:-5}" \
  --max-time "${WEBHOOK_TIMEOUT_SECONDS:-15}" \
  --request POST --header 'Content-Type: application/json' \
  --data "$payload" "$WEBHOOK_URL") || {
    echo "ERRO: não foi possível enviar notificação webhook." >&2
    exit 1
  }
if [[ ! "$http_code" =~ ^2[0-9][0-9]$ ]]; then
  echo "ERRO: webhook retornou HTTP $http_code." >&2
  exit 1
fi
echo "Notificação webhook enviada (HTTP $http_code)."
