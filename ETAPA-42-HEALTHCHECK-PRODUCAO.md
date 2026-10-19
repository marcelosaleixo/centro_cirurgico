# Etapa 42 — Health check e correção de configuração de administrador

## O que foi feito
1. Adicionado `spring-boot-starter-actuator`.
2. Exposto somente o endpoint `/actuator/health` (incluindo os probes de readiness/liveness).
3. Liberados publicamente apenas `/actuator/health` e seus subcaminhos na configuração de segurança. Outros endpoints Actuator não são expostos.
4. Adicionado `HEALTHCHECK` no Dockerfile, verificando `/actuator/health/readiness`.
5. Corrigida a referência circular das propriedades do administrador: as chaves internas são `app.bootstrap.admin.username` e `app.bootstrap.admin.password`, com valores fornecidos por `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD`.

## EasyPanel
- Configure o serviço para construir usando o `Dockerfile` da raiz, não Nixpacks.
- Mantenha `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD` nas variáveis de ambiente.
- Para HTTPS, configure `SESSION_COOKIE_SECURE=true`.
- Não coloque senhas reais no repositório.

## Verificação após o deploy
1. Confirme que o log de build usa `eclipse-temurin:21-jdk-jammy` e `eclipse-temurin:21-jre-jammy`.
2. Abra `/actuator/health` no domínio do sistema: a resposta esperada é `{"status":"UP"}`.
3. No painel do EasyPanel, confira se o container fica saudável após a inicialização.
4. Se a aplicação não subir, confira o primeiro `Caused by` do log de runtime. Health check não corrige falhas de conexão com o PostgreSQL ou migrações.

## Segurança
Somente o estado de saúde é exposto; detalhes internos do health check ficam ocultos. Não exponha outros endpoints Actuator publicamente.

## Testes
Não foi possível executar build Docker ou testes Maven neste ambiente. Execute `./mvnw clean test` localmente e valide o deploy antes de produção.
