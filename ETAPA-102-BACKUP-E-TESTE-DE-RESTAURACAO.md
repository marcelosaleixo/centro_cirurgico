# Etapa 102 — Backup PostgreSQL e ensaio de restauração

## Objetivo

Criar um backup PostgreSQL em formato custom, verificar se o arquivo pode ser listado pelo `pg_restore` e oferecer um ensaio explícito de restauração em um banco descartável. A verificação estrutural do arquivo não substitui uma restauração real.

## Pré-requisitos

- Cliente PostgreSQL instalado (`pg_dump`, `pg_restore` e `psql`) e disponível no `PATH`.
- Conectividade com o servidor PostgreSQL.
- Variáveis de ambiente `PGHOST`, `PGPORT` (opcional, padrão 5432), `PGDATABASE`, `PGUSER` e, se apropriado, `PGPASSWORD`. Evite colocar senhas em scripts, comandos compartilhados ou logs.
- Permissão suficiente para fazer backup; para ensaio, permissão de criar banco de dados.

## 1. Criar e verificar backup

Na raiz do projeto, abra PowerShell:

```powershell
$env:PGHOST = "host-do-postgres"
$env:PGPORT = "5432"
$env:PGDATABASE = "centrocirurgico"
$env:PGUSER = "usuario_backup"
# Configure a senha por um mecanismo seguro da sua infraestrutura.
.\scripts\backup-postgresql.ps1
```

O script grava um arquivo `.dump` em `backups/` e um relatório com tamanho e SHA-256. O SHA-256 ajuda a detectar alterações no arquivo, mas não prova que o banco pode ser restaurado.

## 2. Ensaio de restauração

Use um nome novo para um banco de teste descartável, nunca o banco de produção:

```powershell
.\scripts\test-restore-postgresql.ps1 `
  -BackupFile ".\backups\centrocirurgico-AAAA-MM-DD-HHMMSS.dump" `
  -TargetDatabase "centrocirurgico_restore_teste_01" `
  -ConfirmRestore -WhatIf
```

`-WhatIf` apenas simula a operação. Depois de conferir host, usuário, arquivo e nome do banco, execute novamente sem `-WhatIf` para realizar a restauração. O script não apaga bancos existentes e recusa usar o valor de `PGDATABASE` como destino. O banco de teste criado é mantido para inspeção e deve ser removido manualmente após aprovação, por um administrador autorizado.

## 3. Critérios de aprovação

- Backup não vazio e listagem `pg_restore --list` concluída com código zero.
- Restauração em banco descartável concluída sem erro.
- Banco restaurado acessível e revisado; verificar tabelas, migrações Flyway e amostras de dados relevantes.
- Guardar backup e relatório em armazenamento protegido, separado do servidor da aplicação, com retenção definida.
- Agendar backups e testar restauração periodicamente; não considerar um backup válido apenas porque o comando terminou.

## Limitações

- Não configura agendamento automático, criptografia, política de retenção ou envio para armazenamento externo.
- Não executa nem valida Flyway automaticamente após a restauração.
- Não foi executado contra o PostgreSQL do usuário. Validar em ambiente controlado antes de usar em produção.
