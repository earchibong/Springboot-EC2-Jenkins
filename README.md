## Deploy Spring Boot MongoDB Application on AWS Elastic Beanstalk using Jenkins CI/CD
The idea for this project came from a freelance job request that reads as follows:

Java Spring Boot App. Database will Be MongoDB. Security Provider is Keycloak. Create a configuration for this. Providing CI/CD to AWS as well as Dockerfile / Dockercompose.

**Access full documentation for this project <a href="https://github.com/earchibong/springboot_project/documentation">here</a>**

<br>

## Here are my proposed steps for this configuration:


1. Create a Dockerfile for the application.

```Dockerfile
# Base image
FROM openjdk:11-jdk-slim

# Create app directory
WORKDIR /app

# Copy application JAR to the container
COPY target/my-app.jar .

# Expose port 8080
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "my-app.jar"]
```

This Dockerfile assumes that you have already built the application JAR using `mvn clean package`.

2. Create a GitHub repository for the application and push the code to it.

3. Install Jenkins on an EC2 instance or a server.

4. Install the necessary plugins for the pipeline: Git, Maven, AWS Elastic Beanstalk, Docker, Keycloak, AWS ECR.

5. Create an ECR repository to store the Docker image.

6. Create a Jenkins pipeline job and configure the pipeline to use the GitHub repository as the source.

7. Set up the pipeline to build the application using Maven and package it into a JAR.

8. Build a Docker image using the Dockerfile and the JAR file, and push it to the ECR repository.

```Jenkinsfile
pipeline {
  agent {
    docker {
      image 'maven:3.8-jdk-11'
      args '-v /root/.m2:/root/.m2'
    }
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Docker Build and Push') {
      steps {
        withCredentials([[
          credentialsId: 'aws-ecr-creds',
          accessKeyVariable: 'AWS_ACCESS_KEY_ID',
          secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
        ]]) {
          sh 'docker build -t my-app .'
          sh 'docker tag my-app:latest <your-ecr-repository-url>/my-app:latest'
          sh 'docker push <your-ecr-repository-url>/my-app:latest'
        }
      }
    }
    stage('Deploy to Elastic Beanstalk') {
      when {
        branch 'main'
      }
      environment {
        AWS_ACCESS_KEY_ID = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
      }
      steps {
        withAWS(region: 'us-east-1') {
          elasticBeanstalk(
            applicationName: 'my-app',
            environmentName: 'my-app-prod',
            bucketName: 'my-app-deploy',
            versionLabel: "${env.BUILD_NUMBER}",
            waitForCompletion: true,
            includeEnvironmentVariables: true,
            keycloak: [
              url: 'https://keycloak.mycompany.com/auth',
              realm: 'myrealm',
              clientId: 'my-app'
            ],
            s3Upload: true
          )
        }
      }
    }
  }
}
```

9. Configure the Elastic Beanstalk environment with the appropriate settings for MongoDB and Keycloak.

With these steps completed, you should now have a Jenkins CI/CD pipeline set up to automatically build and deploy your Java Spring Boot application to AWS Elastic Beanstalk using Docker and an ECR repository.
