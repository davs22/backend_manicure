# Etapa 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# Dá permissão e compila o projeto (pula testes para ser mais rápido)
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]