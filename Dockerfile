FROM openjdk:17-jdk-alpine
MAINTAINER https://github.com/PaHod
COPY build/libs/*SNAPSHOT.jar ./resource-service.jar
ENTRYPOINT ["java", "-jar", "resource-service.jar"]
