FROM gradle:7.4-jdk17 AS build

WORKDIR /buildApp
COPY . .
RUN gradle test --no-daemon
RUN gradle build --no-daemon

FROM openjdk:17.0-slim

EXPOSE 8080

USER root

RUN mkdir /app

COPY --from=build /buildApp/build/libs/*.jar /app/backend.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/backend.jar"]
