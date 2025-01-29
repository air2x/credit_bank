FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Сначала собираем bank-my-library
RUN mvn clean install -f bank-my-library/pom.xml

# Затем собираем calculator_microservice
RUN mvn clean package -f calculator_microservice/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /app

#COPY target/*.jar API_Gateway_microservice-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar calculator_microservice-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "calculator_microservice-0.0.1-SNAPSHOT.jar"]
