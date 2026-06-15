FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/truck-backend-1.0.0.jar app.jar
RUN mkdir -p /app/data
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
