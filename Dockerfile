# Etapa de build: compila el proyecto y genera el jar
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom y código
COPY pom.xml .
COPY src ./src

# Compilamos sin tests y generamos el jar
RUN mvn -q -DskipTests clean package

# Etapa de runtime: imagen liviana solo con el JDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el jar desde la etapa de build
COPY --from=build /app/target/gestion-hotelera-0.0.1-SNAPSHOT.jar app.jar

# Puerto donde corre tu app
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","/app.jar"]
