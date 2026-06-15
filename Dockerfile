# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/truck-backend-1.0.0.jar app.jar
RUN mkdir -p /app/data
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
