FROM openjdk:23-slim

WORKDIR /app

COPY target/number-generate-service-0.0.1-SNAPSHOT.jar number-generate-service.jar

CMD ["java", "-jar", "number-generate-service.jar"]