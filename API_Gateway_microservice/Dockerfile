FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests
COPY target/*.jar API_Gateway_microservice-0.0.1-SNAPSHOT.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "API_Gateway_microservice-0.0.1-SNAPSHOT.jar"]
