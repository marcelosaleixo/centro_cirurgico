# Etapa 15 — Regras operacionais por feriado

## O que foi implementado
- Cadastro de regra operacional para cada feriado: `FUNCIONAMENTO_NORMAL`, `ESCALA_ESPECIAL` ou `PAGAMENTO_DIFERENCIADO`.
- Campo opcional de percentual adicional, validado para não aceitar valores negativos.
- A listagem de feriados exibe a regra configurada e o percentual quando informado.
- A geração da escala consulta os feriados cadastrados ativos e acrescenta a regra na observação do plantão, inclusive para feriados anuais recorrentes.
- Nova migração Flyway `V6__regras_operacionais_feriados.sql`; não edita migrações anteriores.

## Importante
Esta etapa registra e apresenta a regra para orientar o RH. Não altera automaticamente a jornada, não concede folga e não calcula folha/pagamento, porque essas decisões dependem das regras internas e da validação do RH/contabilidade. A escala gerada continua seguindo o regime de trabalho do colaborador.

## Como aplicar
1. Faça uma cópia de segurança do banco antes de atualizar.
2. Extraia o projeto e mantenha as migrações V1–V5 existentes.
3. Inicie a aplicação. O Flyway deve aplicar a V6 automaticamente.
4. Acesse `/feriados`, edite ou cadastre um feriado e escolha a regra operacional.
5. Para que a regra apareça em novas observações de escala, gere ou regenere o período conforme as regras atuais do sistema. Registros existentes não são reescritos automaticamente.

## Percentual adicional
O campo é opcional e só é mantido quando a regra é `PAGAMENTO_DIFERENCIADO`. Informe o percentual confirmado pela política do hospital/RH.
