# Etapa 83 — Bloqueio central de consumo cirúrgico

## Implementação
- Criado `BloqueioConsumoCirurgiaService` como ponto único de validação da regra de negócio.
- O registro de consumo por cirurgia chama o serviço central antes de alterar lote, saldo geral ou criar movimentações.
- Fechamento com status `APROVADO` bloqueia a operação e informa que é necessária reabertura autorizada com justificativa.
- A validação ocorre na camada de serviço, não apenas na interface web.
- Não há nova migração Flyway nesta etapa.

## Testes manuais recomendados
1. Registre um consumo em uma cirurgia sem fechamento aprovado.
2. Aprove o fechamento como usuário autorizado.
3. Tente registrar outro consumo pela tela e confirme que não altera estoque nem lotes.
4. Tente enviar o POST diretamente e confirme o mesmo bloqueio no serviço.
5. Reabra o fechamento com justificativa e confirme que o registro volta a ser permitido.
6. Confira os saldos dos lotes, do estoque geral e as movimentações antes/depois de cada tentativa.

## Limites conhecidos
- Esta regra protege o fluxo de consumo vinculado a uma cirurgia (`RastreabilidadeCirurgiaService`). Movimentações genéricas de estoque sem identificador de cirurgia não podem ser bloqueadas por cirurgia específica, pois não carregam esse vínculo.
- A etapa não adiciona exclusão/edição de consumos, pois o fluxo existente só registra consumo. Se essas operações forem adicionadas futuramente, deverão chamar a mesma validação central.
- Antes de produção hospitalar, executar compilação Maven, testes automatizados e testes integrados com PostgreSQL.
