FROM openjdk:11-jdk

WORKDIR /app

COPY target/mongodb-springboot-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mongodb-springboot-1.0.0-SNAPSHOT.jar"]

