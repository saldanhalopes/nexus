# Etapa 1: Build (Maven)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker nas dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila o JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime (JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o JAR gerado na etapa de build
# O wildcard ajuda se a versão mudar levemente no pom.xml
COPY --from=build /app/target/treinamentos-*.jar app.jar

EXPOSE 8080

# Configurações de ambiente para o Spring
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]