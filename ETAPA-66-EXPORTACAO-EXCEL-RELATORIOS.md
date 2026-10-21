# Etapa 66 — Exportação Excel dos relatórios de consumo

## O que mudou
- Adicionado endpoint `GET /mutiroes/relatorios-consumo/exportar-excel`.
- Gera arquivo `.xlsx` com solicitação, mutirão, status, item, categoria, unidade, quantidades solicitada/separada/pendente e situação da conferência.
- O Excel respeita os mesmos filtros de status, categoria e busca aplicados na tela.
- Adicionado botão “Exportar Excel” no relatório de consumo.
- Reutiliza a dependência Apache POI já presente no `pom.xml`; não exige migration de banco.

## Teste
1. Execute `./mvnw clean test` (Windows: `\.\mvnw.cmd clean test`).
2. Inicie a aplicação e abra `/mutiroes/relatorios-consumo`.
3. Aplique filtros e clique em “Exportar Excel”.
4. Confirme que o arquivo baixa e contém somente as linhas correspondentes aos filtros.

O arquivo foi empacotado e verificado quanto à integridade. A compilação Maven e os testes integrados não foram executados nesta geração.
