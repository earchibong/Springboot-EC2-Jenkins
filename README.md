## Deploy Spring Boot MongoDB Application on AWS Elastic Beanstalk using Jenkins CI/CD
The idea for this project came from a freelance job request that reads as follows:

Java Spring Boot App. Database will Be MongoDB. Security Provider is Keycloak. Create a configuration for this. Providing CI/CD to AWS as well as Dockerfile / Dockercompose.

**Access full documentation for this project <a href="https://github.com/earchibong/springboot_project/documentation.md">here</a>**

<br>

There are several service options for deploying this setup to AWS and they include:

- **Elastic Beanstalk:** This service is a Platform-as-a-Service (PaaS) that allows you to quickly deploy and manage web applications. Elastic Beanstalk provides a simple and fast way to deploy Java applications, including Spring Boot applications, to AWS. To deploy the application using Elastic Beanstalk, you can use the AWS Management Console or the AWS CLI.

- **Amazon ECS:** This service is a fully-managed container orchestration service that supports Docker containers. You can use Amazon ECS to deploy and manage Docker containers that run your Spring Boot application. This option requires more setup and configuration than Elastic Beanstalk, but it provides greater flexibility and control over your infrastructure.

- **AWS Lambda:** This service allows you to run your code without provisioning or managing servers. You can use AWS Lambda to deploy the Spring Boot application as a serverless application. However, this option is mostly ideal for applications that have low traffic and short execution times. If the application has more complex dependencies, requires longer execution times, or has high traffic, AWS Lambda may not be the best option.

In this case, I decided to go with `Amazon ECS` because of the flexibility and infrastructure control it offers but without the additional complexity of managing a Kubernetes cluster.

<br>


**Here's the proposed architecture:**

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/89976418-b666-4409-83aa-66ea61c53487)

<br>


## Here are my proposed steps for this configuration:

Here are the steps to set up a Jenkins CI/CD pipeline to AWS ECR and ECS for your Java Spring Boot app with MongoDB and Keycloak:

1. Set up an EC2 instance with Docker and Jenkins installed. 

2. Create an IAM user for Jenkins to access AWS services. Give the user the necessary permissions to access ECR and ECS.

3. Create an ECR repository for Docker image.

4. Create a Dockerfile for the application.

```

FROM openjdk:11-jdk

WORKDIR /app

COPY target/myapp.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]


```

This Dockerfile uses the official OpenJDK 11 image as the base, copies your Spring Boot app's JAR file into the container, and sets the entry point to start the app.

5. Create a Jenkins job for your CI/CD pipeline. Here's an overview of the steps you can include in the job:

- Check out the source code from your GitHub repository.
- Build the Spring Boot app with Maven or Gradle.
- Build the Docker image and tag it with the ECR repository URL.
- Push the Docker image to the ECR repository.
- Deploy the Docker image to ECS using a task definition and a service.

<br>
