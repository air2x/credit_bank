FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests
COPY target/*.jar bank-my-library-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "bank-my-library-1.0-SNAPSHOT.jar"]
