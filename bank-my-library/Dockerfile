FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

#COPY target/*.jar API_Gateway_microservice-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar bank-my-library-1.0-SNAPSHOT.jar
#COPY target/*.jar bank-my-library-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "bank-my-library-1.0-SNAPSHOT.jar"]
