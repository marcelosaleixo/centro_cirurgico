# Etapa 41 — build reproduzível com Java 21 (evita seleção automática do JDK 17 pelo Nixpacks)
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -B clean package -DskipTests \
    && JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /app/application.jar

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
ENV JAVA_OPTS=""
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/application.jar /app/application.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl --fail --silent http://127.0.0.1:${PORT:-8080}/actuator/health/readiness || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/application.jar"]
