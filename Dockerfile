# Etapa 1: Compilar o projeto
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Define encoding UTF-8
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8

# Etapa 2: Rodar a aplicação
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/portalAcademico-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]