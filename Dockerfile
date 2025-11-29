# Etapa 1: Build (Otimizada para Cache)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# 1. COPIA APENAS ARQUIVOS ESSENCIAIS DO MAVEN (pom.xml, mvnw e pasta .mvn)
# Se estes não mudarem, o Docker reutiliza o cache nas próximas linhas.
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dá permissão ao mvnw
RUN chmod +x mvnw

# Baixa as dependências e armazena no cache local do Maven
# Se o pom.xml não mudou, esta camada é reutilizada.
RUN ./mvnw dependency:go-offline

# 2. COPIA O RESTANTE DO CÓDIGO
# Esta é a camada que mais muda, garantindo que a compilação só rode se o código mudar.
COPY src src

# Compila o projeto (pula testes para ser mais rápido)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# COPIA o JAR gerado no estágio 'build'
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]