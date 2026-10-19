#!/usr/bin/env bash
set -Eeuo pipefail
: "${BACKUP_DIR:=/backups}"
HISTORY_FILE="${BACKUP_STATUS_FILE:-${BACKUP_DIR%/}/backup-operations.csv}"
LIMIT="${REPORT_LIMIT:-20}"
if ! [[ "$LIMIT" =~ ^[0-9]+$ ]] || (( LIMIT < 1 )); then echo "ERRO: REPORT_LIMIT deve ser inteiro positivo." >&2; exit 2; fi
if [[ ! -s "$HISTORY_FILE" ]]; then echo "Ainda não há histórico de operações em: $HISTORY_FILE"; exit 0; fi
echo "Histórico de operações de backup (até $LIMIT registros mais recentes)"
echo "Arquivo: $HISTORY_FILE"
if command -v python3 >/dev/null 2>&1; then
  python3 - "$HISTORY_FILE" "$LIMIT" <<'PYCODE'
import csv, sys
path, limit = sys.argv[1], int(sys.argv[2])
with open(path, newline='', encoding='utf-8') as f:
    rows = list(csv.DictReader(f))
if not rows:
    print('Nenhuma operação registrada.')
else:
    print(f"{'INÍCIO UTC':20} {'AÇÃO':13} {'STATUS':8} {'SEG':>6} BANCO")
    for r in rows[-limit:]:
        print(f"{r.get('started_utc','')[:19]:20} {r.get('action','')[:13]:13} {r.get('status','')[:8]:8} {r.get('duration_seconds',''):>6} {r.get('db_name','')}")
    latest = rows[-1]
    print(f"\nÚltima execução: {latest.get('status')} — {latest.get('finished_utc')} UTC")
    failures = sum(1 for r in rows if r.get('status') == 'FAILURE')
    print(f"Falhas registradas no histórico: {failures}")
PYCODE
else
  echo "Aviso: python3 não encontrado; exibindo linhas brutas."
  tail -n "$LIMIT" "$HISTORY_FILE"
fi
