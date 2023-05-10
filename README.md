## Deploy Spring Boot MongoDB Application on AWS Elastic Beanstalk using Jenkins CI/CD
The idea for this project came from a freelance job request that reads as follows:

Java Spring Boot App. Database will Be MongoDB. Security Provider is Keycloak. Create a configuration for this. Providing CI/CD to AWS as well as Dockerfile / Dockercompose.

**Access full documentation for this project <a href="https://github.com/earchibong/springboot_project/documentation.md">here</a>**

<br>

## Here are my proposed steps for this configuration:


To set up a Jenkins CI/CD pipeline for a Java Spring Boot application with MongoDB as the database and Keycloak for security, you can follow the steps below:

1. Clone the GitHub repository containing the Java Spring Boot application.

2. Create a Dockerfile for the application.

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

3. Create an AWS Elastic Beanstalk environment to deploy the application.

4. Create an ECR repository to store the Docker image.

5. Install Jenkins on an EC2 instance or a server.

6. Install the necessary plugins for the pipeline: Git, Maven, Docker, Keycloak, AWS Elastic Beanstalk.

7. Create a Jenkins pipeline job and configure the pipeline to use the GitHub repository as the source.

8. Set up the pipeline to build the application using Maven and package it into a JAR.

9. Build a Docker image using the Dockerfile and the JAR file, and push it to the ECR repository.

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
        APP_NAME = 'my-app'
        ENV_NAME = 'my-app-env'
        DOCKER_IMAGE = "<your-ecr-repository-url>/my-app:latest"
      }
      steps {
        ebDeploy(
          awsAccessKeyId: AWS_ACCESS_KEY_ID,
          awsSecretAccessKey: AWS_SECRET_ACCESS_KEY,
          region: 'us-east-1',
          applicationName: APP_NAME,
          environmentName: ENV_NAME,
          versionLabel: 'jenkins-' + env.BUILD_NUMBER,
          description: 'Deploying my-app from Jenkins',
          sourceBundle: '',
          additionalArguments: "--image-url $DOCKER_IMAGE"
        )
      }
    }
  }
}
```

10. Set up Keycloak for authentication and authorization, and configure the application to use Keycloak for security.

11. Test the pipeline by making a code change and pushing it to the GitHub repository. The pipeline should automatically build and deploy the updated application to Elastic Beanstalk.

Note that this pipeline assumes that you have already set up the necessary infrastructure components, including the AWS Elastic Beanstalk environment, the ECR repository, and the Keycloak server.
