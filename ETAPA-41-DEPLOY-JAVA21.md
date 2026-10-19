# Etapa 41 — Deploy reproduzível com Java 21 no EasyPanel

## Problema endereçado
O log de implantação mostrava `setup | jdk17, maven`, enquanto o `pom.xml` declara Java 21. O Dockerfile deste pacote fixa Java 21 tanto no build quanto no runtime, evitando depender da detecção automática do Nixpacks.

## Como usar no EasyPanel
1. Faça backup do projeto e confirme que `Dockerfile` está na raiz do repositório, ao lado de `pom.xml` e `mvnw`.
2. Envie/commit o Dockerfile e `.dockerignore` para o repositório conectado ao EasyPanel.
3. Em configurações de build, selecione Dockerfile, se houver opção de estratégia de build. O log novo deve mostrar `eclipse-temurin:21-jdk-jammy` e `eclipse-temurin:21-jre-jammy`, não `setup jdk17`.
4. Mantenha as variáveis do PostgreSQL e do administrador na seção de ambiente do EasyPanel; não coloque segredos no Dockerfile nem no Git.
5. Faça novo deploy.

## Variáveis esperadas em runtime
- `PORT` (EasyPanel pode definir automaticamente; fallback 8080)
- `DB_URL` (endereço interno do serviço PostgreSQL)
- `DB_USERNAME`
- `DB_PASSWORD`
- `RH_ADMIN_USERNAME`
- `RH_ADMIN_PASSWORD`
- `SESSION_COOKIE_SECURE=true` quando acessado por HTTPS

O `application.properties` deve mapear `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD` para `app.bootstrap.admin.username` e `app.bootstrap.admin.password`, evitando placeholders circulares.

## Importante
O Dockerfile compila com `-DskipTests` para seguir o comportamento do build anterior. Rode `./mvnw clean test` em ambiente de desenvolvimento antes de produção. Este pacote foi montado e o ZIP foi validado estruturalmente; não foi possível executar um build Docker real neste ambiente. Se o deploy ainda falhar, envie as últimas linhas do log da etapa que falhou.
