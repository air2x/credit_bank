FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY ./bank-my-library/pom.xml ./bank-my-library/
COPY ./bank-my-library/src ./bank-my-library/src/

COPY ./dossier_microservice/pom.xml .
COPY ./dossier_microservice/src ./src

RUN mvn clean install -f bank-my-library/pom.xml
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /app/target/dossier_microservice-*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]