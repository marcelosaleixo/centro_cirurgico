# Correção 2 — Escalas geradas não apareciam no calendário

## Sintoma
A aplicação informava que 22 escalas foram geradas, mas as células do calendário continuavam vazias.

## Ajuste
O controller agora indexa as escalas por data usando uma chave `String` no padrão ISO `yyyy-MM-dd`. O template Thymeleaf cria a mesma chave para cada dia antes de consultar o mapa. Isso elimina a dependência da conversão/comparação de chaves `LocalDate` pelo SpringEL.

A geração de escalas e a persistência no PostgreSQL não foram alteradas nesta correção.

## Teste
1. Substitua os arquivos pelo conteúdo deste pacote.
2. Execute `.\mvnw.cmd clean package -DskipTests`.
3. Reinicie a aplicação.
4. Abra `/escalas/calendario?mes=2026-10&colaboradorId=3`.
5. As escalas já salvas para esse colaborador em outubro de 2026 devem aparecer sem precisar gerar novamente.

Se ainda não aparecer, confira `/escalas?inicio=2026-10-01&fim=2026-10-31&colaboradorId=3`. Se a listagem também estiver vazia, a próxima verificação deve ser feita diretamente no banco (`SELECT id, colaborador_id, data_plantao, status FROM escala_plantao WHERE colaborador_id = 3 AND data_plantao BETWEEN '2026-10-01' AND '2026-10-31' ORDER BY data_plantao`).
