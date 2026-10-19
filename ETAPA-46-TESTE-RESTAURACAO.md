# Etapa 46 — Ensaio controlado de restauração PostgreSQL

## Objetivo
Executar um ensaio de recuperação a partir do backup mais recente, restaurando-o **somente em um banco de teste separado**. O script valida o checksum, a leitura do catálogo e a restauração com `pg_restore --exit-on-error`, e confirma que o destino contém tabelas de usuário.

## Segurança obrigatória
- Crie previamente um banco PostgreSQL exclusivo para o ensaio, por exemplo `centro_restore_test`.
- O usuário configurado precisa ter permissão para criar/alterar objetos dentro desse banco de teste.
- O nome de destino deve terminar em `restore_test` e não pode ser igual ao banco de origem.
- O comando usa `--clean --if-exists`: ele pode apagar objetos existentes **no banco de teste**. Não use um banco com dados necessários e nunca informe o banco de produção como destino.
- O script não cria o banco e não altera o banco de origem; não configure esse ensaio sem confirmar o destino.

## Execução manual
Com os scripts disponíveis e `psql`, `pg_restore`, `sha256sum` e `stat` instalados:

```bash
export BACKUP_DIR=/backups
export DB_NAME=nome_do_banco_origem
export DB_HOST=host_postgres
export DB_PORT=5432
export DB_USERNAME=usuario_postgres
export DB_PASSWORD='senha'
export DRILL_DB_NAME=centro_restore_test
export DRILL_CONFIRM=SIM_RESTAURAR_BANCO_DE_TESTE
bash scripts/restore-drill.sh
```

Use a senha por variável de ambiente/secret do ambiente, não a grave em arquivos versionados nem em documentação. Em EasyPanel, use o hostname interno do serviço PostgreSQL.

## Agendamento no EasyPanel
É preferível começar com execução manual e, depois, criar um job separado semanal para o ensaio. Use a imagem baseada em `scripts/Dockerfile.backup`, monte o volume `/backups` e sobrescreva o comando para `/opt/backup/restore-drill.sh`. Copie o script para a imagem, configure as variáveis acima como secrets e habilite notificações de falha. O banco de teste precisa existir e estar acessível pelo job. O script não agenda o job automaticamente.

## Critérios de sucesso
1. O checksum do dump confere.
2. `pg_restore --list` consegue ler o catálogo.
3. A restauração termina sem erro.
4. A consulta de validação encontra pelo menos uma tabela de usuário.

Após sucesso, execute verificações funcionais da aplicação em ambiente isolado. Uma contagem de tabelas não garante que todos os registros e fluxos estejam corretos.

## Validação desta entrega
O ZIP foi validado estruturalmente. Não foi possível executar o ensaio contra um PostgreSQL real nem construir a imagem Docker neste ambiente.
