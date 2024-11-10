FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT [ "java", "-jar", "/app/backend-0.0.1-SNAPSHOT.jar" ]
EXPOSE 8080