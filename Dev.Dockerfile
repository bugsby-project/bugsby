FROM gradle:7.4-jdk17 AS build

WORKDIR /buildApp
COPY . .
RUN gradle test --no-daemon
RUN cd Server
RUN gradle build --no-daemon

FROM openjdk:17.0-slim

EXPOSE 8080

USER root

RUN mkdir /app

COPY --from=build /buildApp/Server/build/libs/*.jar /app/backend.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app/backend.jar"]
