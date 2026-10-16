# Etapa 32 — Histórico detalhado de alterações

## O que inclui
- Migração Flyway V14 cria `auditoria_alteracoes` e índices.
- Registra diferenças de campos operacionais do cadastro de colaboradores (criação e edição).
- Registra usuário autenticado, data/hora, entidade, identificador, operação, campo e valores anterior/novo.
- Não registra CPF, e-mail ou telefone nessa trilha de valores.
- Tela administrativa paginada em `/admin/auditoria-alteracoes`, exclusiva para `ADMIN_RH`.
- A auditoria genérica de operações da Etapa 31 permanece ativa.

## Instalação e teste
1. Faça backup do código e do banco.
2. Extraia o projeto em uma pasta separada.
3. Execute `./mvnw clean test` (Windows: `\.\mvnw.cmd clean test`).
4. Inicie com `./mvnw spring-boot:run` (Windows: `\.\mvnw.cmd spring-boot:run`).
5. Com usuário `ADMIN_RH`, edite um colaborador e confira `/admin/auditoria-alteracoes`.

## Limite desta etapa
O histórico de valores antes/depois foi integrado ao cadastro de colaboradores. Os demais módulos continuam com auditoria de operações da Etapa 31 e poderão receber comparação detalhada em etapas posteriores. Não são armazenados snapshots completos nem conteúdo de formulários.
