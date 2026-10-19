#!/usr/bin/env bash
set -Eeuo pipefail
umask 077

# Monitor de disponibilidade executado por um job agendado separado.
# Notifica apenas quando o estado muda (UP <-> DOWN), evitando alertas repetidos.
: "${HEALTHCHECK_URL:=}"
: "${HEALTH_STATE_FILE:=/backups/application-health-state}"
: "${WEBHOOK_URL:=}"
: "${HEALTH_CONNECT_TIMEOUT_SECONDS:=5}"
: "${HEALTH_TIMEOUT_SECONDS:=10}"
: "${WEBHOOK_CONNECT_TIMEOUT_SECONDS:=5}"
: "${WEBHOOK_TIMEOUT_SECONDS:=15}"

if [[ -z "$HEALTHCHECK_URL" ]]; then
  echo "ERRO: configure HEALTHCHECK_URL (ex.: https://seu-dominio/actuator/health)." >&2
  exit 2
fi
command -v curl >/dev/null 2>&1 || { echo "ERRO: curl não está instalado." >&2; exit 2; }

state_dir=$(dirname "$HEALTH_STATE_FILE")
mkdir -p "$state_dir"
previous="UNKNOWN"
if [[ -r "$HEALTH_STATE_FILE" ]]; then
  previous=$(head -n 1 "$HEALTH_STATE_FILE" | tr -d '\r\n')
fi

started=$(date -u +%Y-%m-%dT%H:%M:%SZ)
response_file=$(mktemp)
trap 'rm -f "$response_file"' EXIT
set +e
http_code=$(curl --silent --show-error --output "$response_file" --write-out '%{http_code}' \
  --connect-timeout "$HEALTH_CONNECT_TIMEOUT_SECONDS" --max-time "$HEALTH_TIMEOUT_SECONDS" \
  --get "$HEALTHCHECK_URL" 2>/dev/null)
curl_rc=$?
set -e

if (( curl_rc == 0 )) && [[ "$http_code" =~ ^2[0-9][0-9]$ ]]; then
  # Para Spring Boot Actuator, HTTP 2xx sozinho não basta: status deve ser UP.
  if grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"' "$response_file"; then current="UP"; else current="DOWN"; fi
else
  current="DOWN"
fi

printf '%s\n%s\nhttp_code=%s\nchecked_at_utc=%s\n' "$current" "$previous" "${http_code:-000}" "$started" > "$HEALTH_STATE_FILE.tmp"
mv "$HEALTH_STATE_FILE.tmp" "$HEALTH_STATE_FILE"
echo "Health check: status=$current previous=$previous http=${http_code:-000} timestamp_utc=$started"

if [[ "$current" != "$previous" ]]; then
  if [[ -n "$WEBHOOK_URL" ]]; then
    command -v python3 >/dev/null 2>&1 || { echo "ERRO: python3 necessário para gerar JSON seguro para o webhook." >&2; exit 2; }
    payload=$(python3 - "$current" "$previous" "$started" "${http_code:-000}" "$HEALTHCHECK_URL" <<'PYCODE'
import json, sys
current, previous, timestamp, http_code, url = sys.argv[1:]
print(json.dumps({
    "event": "centro-cirurgico.application-health",
    "status": current,
    "previous_status": previous,
    "timestamp_utc": timestamp,
    "http_code": http_code,
    "healthcheck_url": url,
}, ensure_ascii=False))
PYCODE
)
    set +e
    webhook_code=$(curl --silent --show-error --output /dev/null --write-out '%{http_code}' \
      --connect-timeout "$WEBHOOK_CONNECT_TIMEOUT_SECONDS" --max-time "$WEBHOOK_TIMEOUT_SECONDS" \
      --request POST --header 'Content-Type: application/json' --data "$payload" "$WEBHOOK_URL")
    webhook_rc=$?
    set -e
    if (( webhook_rc != 0 )) || [[ ! "${webhook_code:-000}" =~ ^2[0-9][0-9]$ ]]; then
      echo "AVISO: mudança de estado registrada, mas webhook falhou (HTTP ${webhook_code:-000})." >&2
    else
      echo "Notificação de mudança de estado enviada (HTTP $webhook_code)."
    fi
  else
    echo "AVISO: WEBHOOK_URL não configurado; mudança de estado não notificada." >&2
  fi
fi

# Retorna falha enquanto a aplicação estiver indisponível, permitindo que o agendador
# do EasyPanel marque a execução como falha. A notificação só é enviada em transição.
[[ "$current" == "UP" ]]
