# Etapa 98 — Auditoria de configuração para produção

## Objetivo

Adicionar uma verificação estática reproduzível da configuração de ambiente antes da implantação. A auditoria não altera o banco nem inicia a aplicação.

## Execução

Na raiz do projeto, em PowerShell:

```powershell
# Verificação informativa para desenvolvimento
.\scripts\auditoria-configuracao.ps1

# Auditoria com bloqueios para requisitos de ambiente de produção
.\scripts\auditoria-configuracao.ps1 -Producao
```

No modo `-Producao`, configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` na plataforma de implantação, usando o gerenciador de segredos do ambiente. A variável `RH_ADMIN_PASSWORD` deve seguir o processo seguro de bootstrap de administrador, se esse processo estiver habilitado.

## O que a auditoria verifica

- Presença de `pom.xml`, `application.properties` e diretório Flyway.
- Variáveis essenciais para conexão com PostgreSQL no modo de produção.
- Configurações potencialmente perigosas, como `ddl-auto=update`.
- Avisos sobre fallback local de conexão/senha e arquivos `.env`.

## Limitações

Esta etapa é uma auditoria estática de configuração. Ela não comprova que o PostgreSQL está acessível, que as migrações funcionam, que os endpoints estão seguros ou que o sistema está pronto para produção. Execute também os testes da Etapa 97 em ambiente de homologação e revise cada aviso.
