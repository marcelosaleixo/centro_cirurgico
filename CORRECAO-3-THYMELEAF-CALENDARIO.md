# Correção 3 — Thymeleaf não encontrava as escalas por data

## Causa raiz
O atributo `th:if` era avaliado antes de `th:with` no mesmo elemento. Assim, `chaveData` ainda não existia quando a condição `escalasPorData[chaveData] != null` era avaliada. Por isso, o sistema informava escalas geradas, mas as células continuavam vazias.

## Correção
A variável `chaveData` foi movida para o elemento pai de cada dia do calendário. Dessa forma, ela é definida antes de os elementos filhos consultarem o mapa.

## Como aplicar
1. Extraia este ZIP sobre o projeto, substituindo os arquivos quando solicitado.
2. Execute:
   ```powershell
   .\mvnw.cmd clean package -DskipTests
   .\mvnw.cmd spring-boot:run
   ```
3. Abra o calendário de outubro de 2026 com o colaborador que já teve a escala gerada. Não precisa gerar novamente.

A correção não altera nem apaga os dados do PostgreSQL.
