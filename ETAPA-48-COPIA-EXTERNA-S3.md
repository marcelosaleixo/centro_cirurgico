# Etapa 48 — Cópia externa dos backups

## Objetivo

Manter uma cópia dos backups fora do volume local do servidor, reduzindo o risco de perda conjunta do banco e dos arquivos locais.

## O que foi adicionado

- `scripts/upload-backup-s3.sh`: seleciona o dump local mais recente do banco configurado, valida o SHA-256 local, envia o dump e o checksum e confere o tamanho do objeto remoto.
- `scripts/run-and-record.sh`: nova ação `upload-s3`, registrada no CSV de operações e no status mais recente.
- `scripts/Dockerfile.backup`: instala AWS CLI e inclui o script de envio.
- `README-ETAPA-48.md`: configuração e orientações de segurança.

## Implantação no EasyPanel

1. Atualize o projeto e reconstrua a imagem usando `scripts/Dockerfile.backup`.
2. No job de upload, monte o volume persistente em `/backups`.
3. Configure as variáveis de ambiente e secrets descritos no README.
4. Execute manualmente `/opt/backup/run-and-record.sh upload-s3`.
5. Confirme o objeto `.dump` e o `.dump.sha256` no bucket, e verifique o status do job.
6. Só então agende o upload para depois da conclusão do backup local.

## Importante

O script não cria bucket, não configura políticas IAM, não ativa criptografia/versionamento e não define retenção remota. Essas ações dependem do provedor escolhido. O teste de tamanho remoto é uma verificação básica, não substitui restauração real.
