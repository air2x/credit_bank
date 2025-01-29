FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install -f bank-my-library/pom.xml

RUN mvn clean install -f dossier_microservice/pom.xml

FROM openjdk:17-jdk-slim

WORKDIR /app

#COPY target/*.jar API_Gateway_microservice-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar dossier_microservice-0.0.1-SNAPSHOT.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "dossier_microservice-0.0.1-SNAPSHOT.jar"]
