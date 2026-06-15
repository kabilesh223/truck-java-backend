# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/target/truck-backend-1.0.0.jar app.jar
RUN mkdir -p /app/data
EXPOSE 8000
CMD java -jar app.jar