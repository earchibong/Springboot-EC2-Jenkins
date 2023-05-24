pipeline {
  environment {
    PROJECT     = 'springboot-docker'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
    IMAGE_NAME = "mongodb-springboot"
    IMAGE_TAG = "latest"
    AWS_REGION = "eu-west-2"
    DOCKERFILE = "Dockerfile"
    MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
    COMPOSE_FILE='docker-compose.yml'
  }
  
  agent any
  
  stages {
    
    stage("Initial cleanup") {
        steps {
        dir("${WORKSPACE}") {
            deleteDir()
        }
        }
    }
    
    
    stage('Checkout') {
      steps {
      checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'e1868d62-3cd4-44da-aba1-a24e2183d6e3', url: 'https://github.com/earchibong/springboot_project.git']])
      }
    }
    
    stage('Build Jar file') {
      steps {
        sh "mvn -f ${env.WORKSPACE}/pom.xml clean package -DskipTests"
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
    
    stage('Build Docker image') {
      steps {
        script {
              sh """aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"""
              sh "docker build --tag ${IMAGE_NAME} --file ${DOCKERFILE} ${env.WORKSPACE}"
              docker.withRegistry("https://${ECR_REGISTRY}") {
                docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
              }
        }
      }
    }

    stage('Deploy to EC2') {
      steps {
        script {
              // Connect to the EC2 instance using IAM role
              withAWS(region: 'eu-west2', role: 'arn:aws:iam::350100602815:instance-profile/ECR-Jenkins') {
              // Run the Docker Compose deployment on the EC2 instance
              // sh 'cd /path/to/project && docker-compose pull && docker-compose up -d'
              sh "docker pull ${ECR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
              sh "docker-compose -f ${COMPOSE_FILE} up -d"
              }
        }
      }
    }
  }
}