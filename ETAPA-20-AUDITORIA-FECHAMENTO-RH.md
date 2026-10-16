# Etapa 20 — Histórico de auditoria do fechamento do RH

## Entregas
- Registra mudanças no status de conferência por colaborador, incluindo status anterior, novo status e observação.
- Registra o fechamento definitivo do período.
- Exibe o histórico no próprio relatório de fechamento do RH, filtrado pelas datas consultadas.
- Cria a tabela `auditoria_fechamento_rh` por meio da migration Flyway V9.

## Instalação
1. Faça backup do banco.
2. Extraia o projeto e abra a pasta que contém `pom.xml`.
3. Recarregue o projeto Maven na IDE.
4. Execute `./mvnw clean package -DskipTests` (Windows: `.\mvnw.cmd -U clean package -DskipTests`).
5. Inicie com `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`).
6. A migration V9 será aplicada pelo Flyway na inicialização.

## Observações
- Os eventos são gravados na mesma transação da operação correspondente.
- Nesta etapa o campo `responsavel` recebe `sistema`, pois o fluxo atual não fornece uma identidade autenticada confirmada ao serviço. Para auditoria nominal, integre-o ao usuário autenticado após confirmar a configuração de segurança do projeto.
- Validar compilação e execução localmente antes de publicar em produção.
