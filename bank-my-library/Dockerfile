# Используем образ Maven для сборки
FROM maven:3.9.9-amazoncorretto-17 AS build

# Рабочая директория
WORKDIR /app

# Копируем общую библиотеку из корня проекта
COPY ./bank-my-library/pom.xml ./bank-my-library/
COPY ./bank-my-library/src ./bank-my-library/src/

# Собираем библиотеку и микросервис
RUN mvn clean install -f bank-my-library/pom.xml

# Финальный образ
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/bank-my-library/target/*.jar /app/bank-my-library.jar
CMD ["java", "-jar", "/app/bank-my-library.jar"]
