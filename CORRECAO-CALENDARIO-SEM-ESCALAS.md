# Correção — Calendário sem colaboradores

## Causa
A tela do calendário consultava apenas registros já existentes em `escala_plantao`. Ela não criava escalas automaticamente; por isso, os dias úteis apareciam vazios quando ainda não havia escalas geradas para o mês.

## Correção
- Adicionada a ação `POST /escalas/calendario/gerar`.
- Novo botão na tela do calendário para gerar escalas do mês selecionado.
- Sem filtro de colaborador, gera para todos os colaboradores ativos.
- Com filtro de colaborador, gera apenas para o colaborador selecionado.
- A geração continua respeitando o regime 12x36 ou 5x2, férias, banco de horas, data de referência e escalas canceladas.
- Falhas de configuração/conflitos são mostradas na tela por colaborador.

## Como testar
1. Inicie o sistema e acesse `/escalas/calendario`.
2. Escolha o mês.
3. Clique em **Gerar escala para todos os colaboradores**.
4. Verifique a mensagem de resultado e o calendário.

Se um colaborador não for gerado, confira se está ativo e se tem regime e data de referência cadastrados. No 12x36, confira também o plantão de referência.
