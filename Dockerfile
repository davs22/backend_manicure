# Etapa 1: Build (Otimizada para Cache)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# 1. COPIA ARQUIVOS ESSENCIAIS DO MAVEN: MUDANDO PARA APONTAR PARA A PASTA CORRETA
# O contexto é BACKEND_MANICURE, os arquivos estão em manicure_backend/
COPY manicure_backend/pom.xml .
COPY manicure_backend/mvnw .
COPY manicure_backend/.mvn .mvn

# Dá permissão ao mvnw
RUN chmod +x mvnw

# Baixa as dependências
RUN ./mvnw dependency:go-offline

# 2. COPIA O RESTANTE DO CÓDIGO
# MUDANDO PARA APONTAR PARA A PASTA CORRETA
COPY manicure_backend/src src

# Compila o projeto (pula testes para ser mais rápido)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# COPIA o JAR gerado no estágio 'build'
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]