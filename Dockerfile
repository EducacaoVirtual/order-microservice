FROM eclipse-temurin:21-jdk-jammy

VOLUME /tmp
ARG JAR_FILE=target/order-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application.yml /app/application.yml

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app.jar"]