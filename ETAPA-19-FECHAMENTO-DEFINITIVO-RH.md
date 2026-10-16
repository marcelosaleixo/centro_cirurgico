# Etapa 19 — Fechamento definitivo mensal do RH

## Entrega
- Permite concluir o fechamento de um intervalo de datas.
- Bloqueia o fechamento se não houver escalas ou se algum colaborador estiver PENDENTE/ DIVERGENCIA (somente CONFERIDO permite concluir).
- Registra status, horário e observação do fechamento.
- Impede alterações nas conferências individuais após fechar o período.
- Adiciona migration Flyway V8 e mantém a estrutura de etapas anteriores.

## Como executar
1. Faça backup do banco de dados.
2. Extraia o ZIP e abra a pasta que contém `pom.xml`.
3. Recarregue o Maven na IDE.
4. Execute `./mvnw clean package -DskipTests` (Windows: `\.mvnw.cmd -U clean package -DskipTests`).
5. Inicie a aplicação; Flyway aplicará a V8 automaticamente.
6. Abra `/relatorios/fechamento-rh`, consulte o período, marque todas as conferências como **Conferido** e conclua o fechamento.

## Atenção
O fechamento é bloqueado por período exato (data inicial e final). A tela de fechamento não substitui autorização/perfil de acesso; restrinja a rota de fechamento ao RH/gestão na configuração de segurança antes de usar em produção. Não foi possível confirmar a compilação nesta geração; valide localmente antes de publicar.
