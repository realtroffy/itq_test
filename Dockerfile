# Stage 1: Builder
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /application
# Копируем родительский pom.xml и подмодули (папки с исходниками)
# Копируем основной pom.xml
COPY pom.xml .

# Копируем pom.xml для подмодулей
COPY orders-service/pom.xml orders-service/
COPY number-generate-service/pom.xml number-generate-service/

# Копируем исходные коды подмодулей
COPY orders-service/src orders-service/src/
COPY number-generate-service/src number-generate-service/src/

# Копируем mvnw и .mvn для использования Maven
COPY mvnw ./
COPY .mvn .mvn
# Запуск сборки
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw clean install -Dmaven.test.skip

# Stage 2: Extract layers
FROM eclipse-temurin:23-jre AS layers
WORKDIR /application
# Копируем собранный JAR файл после сборки в предыдущем слое
COPY --from=builder /application/target/*.jar app.jar
# Извлекаем слои для Spring Boot
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Run application
FROM eclipse-temurin:23-jre
VOLUME /tmp

# Копируем слои из предыдущего этапа
COPY --from=layers /application/dependencies/ ./dependencies/
COPY --from=layers /application/spring-boot-loader/ ./spring-boot-loader/
COPY --from=layers /application/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=layers /application/application/ ./application/

# Определяем точку входа
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
