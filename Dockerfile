# --- Etapa 1: Compilación (Build) ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Empaquetamos el proyecto saltando los tests para ir más rápido
RUN mvn -q -DskipTests clean package

# --- Etapa 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# CORRECCIÓN Y MEJORA:
# 1. Usamos *.jar para que detecte el archivo sin importar si es versión 0.0.1 o 1.0.0
# 2. Lo renombramos a app.jar dentro de la carpeta /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# CORRECCIÓN CRÍTICA:
# Quitamos la "/" del inicio.
# Ahora busca "app.jar" en la carpeta actual (/app) en lugar de en la raíz del sistema.
ENTRYPOINT ["java","-jar","app.jar"]