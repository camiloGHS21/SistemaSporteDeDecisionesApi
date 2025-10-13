# Etapa 1: Compilación con Maven
FROM maven:3.9-eclipse-temurin-17-focal AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el wrapper de Maven y el pom.xml para descargar dependencias
COPY .mvn/ .mvn
COPY mvnw.cmd pom.xml ./

# Descargar las dependencias para aprovechar el cache de Docker
RUN mvn dependency:go-offline

# Copiar el resto del código fuente
COPY src ./src

# Compilar y empaquetar la aplicación, omitiendo los tests
RUN mvn clean install -DskipTests


# Etapa 2: Ejecución con JRE
FROM eclipse-temurin:17-jre-jammy

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que corre la aplicación Spring Boot (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
