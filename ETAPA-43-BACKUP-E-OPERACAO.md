# Etapa 43 — Backup e operação segura do PostgreSQL

## Objetivo
Adicionar procedimentos reproduzíveis de backup e restauração para o banco do Centro Cirúrgico, sem alterar as tabelas nem executar operações automaticamente na inicialização da aplicação.

## Arquivos
- `scripts/backup-postgres.sh`: backup custom-format com `pg_dump`, arquivo com permissões restritas e checksum SHA-256.
- `scripts/restore-postgres.sh`: restauração explícita com confirmação digitada `RESTAURAR`.
- `scripts/backup-postgres.ps1`: versão para Windows PowerShell.
- `.env.example`: nomes de variáveis esperadas, sem credenciais reais.

## Variáveis necessárias
Configure `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` e opcionalmente `BACKUP_DIR`. Não use `localhost` se o PostgreSQL estiver em outro contêiner. Use o hostname interno do serviço no EasyPanel.

## Backup no Linux
Instale o cliente PostgreSQL (`pg_dump`), exporte as variáveis no shell/ambiente seguro e execute:
```bash
chmod +x scripts/backup-postgres.sh scripts/restore-postgres.sh
./scripts/backup-postgres.sh
```
O script cria um arquivo `.dump` e um `.sha256`. Guarde cópias fora do contêiner e fora do mesmo volume/disco do banco.

## Backup no Windows PowerShell
Instale as ferramentas cliente do PostgreSQL e defina as variáveis:
```powershell
$env:DB_HOST = "HOST_DO_POSTGRES"
$env:DB_PORT = "5432"
$env:DB_NAME = "centro_cirurgico"
$env:DB_USERNAME = "USUARIO_DO_BANCO"
$env:DB_PASSWORD = "SENHA_DO_BANCO"
$env:BACKUP_DIR = ".\backups"
.\scripts\backup-postgres.ps1
```

## Restauração
Faça primeiro uma cópia do estado atual. Use um ambiente de homologação sempre que possível:
```bash
./scripts/restore-postgres.sh ./backups/arquivo.dump
```
O script solicita confirmação e utiliza `pg_restore --clean --if-exists`; isso pode remover objetos existentes no banco de destino. Nunca restaure sem confirmar host, banco e backup correto.

## Agendamento no EasyPanel
O script não é executado automaticamente pelo contêiner web. Configure um serviço/cron separado com acesso de rede ao PostgreSQL e armazenamento persistente seguro, ou use o mecanismo de backup do provedor de banco. Não salve arquivos de backup apenas no filesystem efêmero do contêiner da aplicação.

## Boas práticas
- Agende backups diários e retenção compatível com a política hospitalar.
- Armazene cópias criptografadas e com acesso restrito, preferencialmente fora do mesmo servidor.
- Teste periodicamente a restauração e registre data, responsável e resultado.
- Não versionar `.env`, senhas nem dumps no Git.
- Esta etapa não cria automaticamente cron, não envia backups para armazenamento externo e não altera o banco.

## Validação
O ZIP foi verificado estruturalmente. Não executei `pg_dump`, uma restauração real, build Docker ou testes Maven neste ambiente.
