FROM openjdk:11-jdk

WORKDIR /app

COPY target/mongodb-springboot.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mongodb-springboot.jar"]

