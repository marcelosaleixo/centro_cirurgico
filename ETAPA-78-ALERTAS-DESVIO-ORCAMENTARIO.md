# Etapa 78 — Alertas de desvio orçamentário

## Entrega
- Nova página `GET /orcamentos/alertas`.
- Parâmetro `tolerancia` em percentual (padrão 10%, faixa aceita de 0 a 500%).
- Comparação do custo previsto com o custo estimado registrado nos até 300 consumos mais recentes, agrupados por procedimento e material.
- Indicadores de quantidade de materiais orçados e de linhas acima do limite configurado.

## Como testar
1. Faça backup do projeto e do banco.
2. Execute a aplicação com a configuração PostgreSQL existente.
3. Cadastre orçamentos e registros de consumo com lotes que tenham custo unitário preenchido.
4. Acesse `/orcamentos/alertas` e teste diferentes limites de tolerância.

## Limitação conhecida
O comparativo é agregado por procedimento/material a partir dos 300 consumos mais recentes; não é um alerta por cirurgia individual nem uma série temporal. Valores de lote sem custo unitário não entram na soma do realizado. Não foi adicionada migração de banco nesta etapa.
