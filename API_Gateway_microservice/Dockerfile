FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY ./bank-my-library/pom.xml ./bank-my-library/
COPY ./bank-my-library/src ./bank-my-library/src/

COPY ./API_Gateway_microservice/pom.xml .
COPY ./API_Gateway_microservice/src ./src

RUN mvn clean install -f bank-my-library/pom.xml
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /app/target/API_Gateway_microservice-*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]