pipeline {
  environment {
    PROJECT     = 'springboot-ecs'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
    IMAGE_NAME = "mongodb-springboot"
    IMAGE_TAG = "latest"
    AWS_REGION = "eu-west-2"
    ECS_CLUSTER = "springboot_project"
    ECS_SERVICE = "springboot_service"
    DOCKERFILE = "Dockerfile"
    TASK_FAMILY = "springboot_task_family"
    MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
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

    stage('deploy') {
      steps {
        echo 'deploy coming soon'
      }
    }
  }
}