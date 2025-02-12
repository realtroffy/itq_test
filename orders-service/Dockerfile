FROM openjdk:23-slim

WORKDIR /app

COPY target/orders-service-0.0.1-SNAPSHOT.jar orders-service.jar

CMD ["java", "-jar", "orders-service.jar"]