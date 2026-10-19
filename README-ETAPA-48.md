# Etapa 48 — Cópia externa de backups para S3

Pacote incremental do Centro Cirúrgico Hospitalar. O novo script `scripts/upload-backup-s3.sh` envia o backup mais recente e seu arquivo `.sha256` para um bucket S3 ou compatível com S3. Depois confere o tamanho do objeto remoto. O wrapper `scripts/run-and-record.sh upload-s3` registra a execução no histórico CSV.

## Variáveis do job

- `BACKUP_DIR=/backups`
- `DB_NAME=nome_do_banco`
- `S3_BUCKET=nome-do-bucket`
- `S3_PREFIX=centro-cirurgico/postgres` (opcional)
- `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` — cadastrar como secrets no EasyPanel, nunca no repositório
- `AWS_DEFAULT_REGION=us-east-1` (ajuste conforme o provedor)
- `S3_ENDPOINT_URL=https://endpoint-do-provedor` (somente se não for AWS S3 padrão)

O job deve montar o mesmo volume persistente `/backups` usado pelo backup local e executar:

```bash
/opt/backup/run-and-record.sh upload-s3
```

## Recomendações de segurança

1. Use credenciais dedicadas com permissão mínima para gravar e consultar objetos apenas no bucket/prefixo escolhido.
2. Habilite criptografia no bucket e, se disponível, versionamento e política de retenção/imutabilidade no provedor.
3. Não compartilhe a mesma credencial com outros serviços e não grave credenciais em arquivos versionados.
4. Agende o envio após o backup local terminar; configure o job para considerar código diferente de zero como falha.
5. O script não apaga objetos remotos. Configure a retenção remota diretamente no provedor, após validar a política, para evitar perda acidental.
6. A checagem do tamanho remoto não prova que o backup pode ser restaurado. Mantenha o ensaio de restauração da Etapa 46.

## Limitações da validação

A entrega foi verificada estruturalmente. A imagem Docker não foi construída e o envio não foi testado contra AWS S3 ou outro provedor real; valide endpoint, região, permissões e política de bucket no ambiente de implantação.
