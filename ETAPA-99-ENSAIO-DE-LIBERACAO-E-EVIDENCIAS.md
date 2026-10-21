# Etapa 99 — Ensaio de liberação e evidências

## Objetivo

Padronizar o ensaio de liberação do Centro Cirúrgico e registrar evidências antes de promover uma versão para produção. Esta etapa não declara a aplicação aprovada: o resultado depende da execução dos comandos e da homologação humana.

## Execução no Windows

Abra o PowerShell na raiz do projeto e execute:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\scripts\ensaio-liberacao.ps1
```

Para incluir as verificações estáticas de configuração de produção:

```powershell
.\scripts\ensaio-liberacao.ps1 -AuditoriaProducao
```

O script grava um relatório em `target\release-evidence\` com horário, comandos, códigos de saída e resultados. Ele não inclui senhas ou valores de variáveis secretas.

## Critérios de aceite manual

- [ ] `clean compile`, testes e empacotamento concluídos sem falhas.
- [ ] Migrações Flyway aplicadas em uma base de homologação restaurável.
- [ ] Login e permissões testados com perfis distintos.
- [ ] Fluxo de escala, férias e troca aprovado por responsável operacional.
- [ ] Movimentação de estoque, carrinho e armário conferida com saldos antes/depois.
- [ ] Cirurgias e mutirões testados contra conflitos de horário e sala.
- [ ] Backup restaurado em ambiente separado; não basta confirmar que o arquivo existe.
- [ ] Menu lateral e páginas críticas verificados em desktop e celular.
- [ ] Segredos, logs e dados de pacientes revisados para evitar exposição.
- [ ] Plano de rollback e responsável pela liberação definidos.

## Política de liberação

Não publique se houver falha de compilação/testes, migração pendente, segredo padrão, backup sem restauração testada ou defeito crítico nos fluxos clínicos. Guarde o relatório gerado junto com a versão e o commit liberado.
