# Etapa 46 — Teste de restauração

Parte da Etapa 45. Adiciona `scripts/restore-drill.sh`, que realiza um ensaio de restauração do backup mais recente em banco de teste explicitamente separado e protegido por verificações de nome/confirmação. Leia `ETAPA-46-TESTE-RESTAURACAO.md` antes de executar. O script é destrutivo para objetos existentes no banco de teste configurado, nunca deve apontar para produção e não é agendado automaticamente.
