# Etapa 34 — Relatórios de auditoria com filtros e exportação Excel

## Entrega
- Filtros por usuário, entidade/módulo, operação, data inicial e data final na trilha `auditoria_alteracoes`.
- Paginação de 30 registros, preservando os filtros ao navegar.
- Exportação `.xlsx` com os filtros aplicados.
- Validação de intervalo de datas, rejeitando data inicial posterior à data final.
- Acesso às rotas permanece restrito a `ADMIN_RH` pela configuração de segurança existente.
- Nenhuma migração nova é necessária.

## Como executar
Na raiz do projeto, no Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Abra `/admin/auditoria-alteracoes`. Preencha os filtros e selecione **Filtrar** ou **Exportar Excel**.

## Notas
- O filtro de operação aceita o nome exato da operação registrada (por exemplo, `CRIACAO`, `EDICAO`, `EXCLUSAO`, `DESATIVACAO` ou tipo de movimentação).
- O arquivo Excel inclui os valores anterior e novo que já estão armazenados na tabela de auditoria. Não foram adicionados novos campos sensíveis.
- A exportação reúne todos os resultados que correspondem aos filtros. Em bases muito grandes, a próxima evolução deve adicionar exportação assíncrona/por lotes.
