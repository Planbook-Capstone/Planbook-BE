# Dockerfile cho Auth Service
FROM openjdk:21-jdk-slim-bullseye
LABEL maintainer="Planbook <planbook@gmail.com>"
WORKDIR /app
COPY target/*.jar auth-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "auth-service.jar"]