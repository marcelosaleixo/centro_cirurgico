# Etapa 45 — Validação de backups

Esta etapa parte da Etapa 44 e acrescenta `scripts/verify-backups.sh`, que confere checksum, leitura do catálogo do dump e idade do backup mais recente. Leia `ETAPA-45-VALIDACAO-BACKUP.md`.

A validação deve ser configurada como job agendado separado no EasyPanel. Ela não é ativada automaticamente e não substitui teste de restauração.
