# Etapa 18 — Conferência individual do fechamento do RH

## O que foi incluído
- Registro persistente da conferência por colaborador e período selecionado.
- Status: Pendente, Conferido ou Com divergência.
- Campo de observação para documentar diferenças encontradas.
- Migration Flyway V7 para criar a tabela e validar os estados permitidos.
- Mantida a exportação Excel e o resumo operacional da etapa anterior.

## Aplicação
1. Faça backup do banco antes de atualizar.
2. Extraia o ZIP e abra a pasta `centrocirurgico`, que contém `pom.xml`.
3. Recarregue o projeto Maven na IDE.
4. No PowerShell, execute:

```powershell
.\mvnw.cmd -U clean package -DskipTests
```

5. Inicie a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

6. Acesse `/relatorios/fechamento-rh`, selecione o período e salve a conferência de cada colaborador.

O Flyway aplicará a migration V7 automaticamente. A implementação não altera os registros de escala, férias, feriados ou banco de horas.
