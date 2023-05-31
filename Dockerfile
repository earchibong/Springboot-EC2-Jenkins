#FROM openjdk:11-jdk

#WORKDIR /app

#COPY target/mongodb-springboot-1.0.0-SNAPSHOT.jar /app

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "/mongodb-springboot-1.0.0-SNAPSHOT.jar"]

# Use a base image with Java and necessary dependencies
FROM adoptopenjdk:11-jre-hotspot

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the container
COPY target/mongodb-springboot.jar app.jar

# Expose the port on which your Spring Boot application listens
EXPOSE 5000

# Set the entry point command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]