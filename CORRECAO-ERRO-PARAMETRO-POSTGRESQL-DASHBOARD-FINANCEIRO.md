# Correção — parâmetro temporal sem tipo no PostgreSQL

## Causa

A consulta JPQL do `ConsumoCirurgiaLoteRepository` usava condições como `(:inicio is null or c.registradoEm >= :inicio)`. Quando o valor era `null`, o PostgreSQL não conseguia inferir o tipo do parâmetro JDBC e retornava SQLState `42P18` ("não foi possível determinar o tipo de dados do parâmetro"). O erro aparecia ao abrir `/relatorios/dashboard-financeiro`.

## Correção aplicada

A consulta com parâmetros opcionais foi substituída por métodos derivados do Spring Data JPA que só recebem parâmetros não nulos. O método `relatorio(cirurgiaId, inicio, fim)` agora seleciona a consulta adequada conforme os filtros presentes, preservando a assinatura usada pelos controllers e mantendo o `EntityGraph` para carregar as associações necessárias.

## Administrador inicial

O log também registra que `RH_ADMIN_PASSWORD` não tem pelo menos 12 caracteres. Isso é um problema separado: configure no ambiente uma senha forte com no mínimo 12 caracteres para que o inicializador possa criar o administrador inicial. Não coloque a senha real no repositório nem neste relatório.

## Validação

Reinicie a aplicação e teste o dashboard financeiro sem datas, com apenas data inicial, apenas data final e com o intervalo completo. Teste também o relatório de consumo filtrando por cirurgia. Execute `mvnw.cmd test` e `mvnw.cmd package` no Windows; esta correção não foi compilada nem testada contra o PostgreSQL neste ambiente.
