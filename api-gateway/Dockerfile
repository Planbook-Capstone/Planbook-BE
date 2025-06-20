# Dockerfile cho API Gateway
FROM openjdk:21-jdk-slim-bullseye
LABEL maintainer="Planbook <planbook@gmail.com>"
WORKDIR /app
COPY target/*.jar api-gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]