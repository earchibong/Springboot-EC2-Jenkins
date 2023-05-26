pipeline {
  environment {
    PROJECT     = 'mongodb-springboot'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
    AWS_REGION = "eu-west-2"
    DOCKERFILE = "./Dockerfile"
    MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
    COMPOSE_FILE = "./docker-compose.yml"
    EC2_INSTANCE = "ec2-user@ec2-52-56-112-70.eu-west-2.compute.amazonaws.com"
    IMAGE_TAG = "mongospringboot-${env.BUILD_ID}"
    IMAGE_NAME = "${ECR_REGISTRY}:${IMAGE_TAG}"
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
               
               //def imageTag = "${env.BUILD_NUMBER}-${env.GIT_BRANCH}-${env.BUILD_ID}"
               //def imageName = "${ECR_REGISTRY}:${imageTag}"

               sh "docker build --tag ${IMAGE_NAME} --file ${DOCKERFILE} ${env.WORKSPACE}"
               docker.withRegistry("https://${ECR_REGISTRY}") {
                docker.image("${IMAGE_NAME}").push() 
                }
          }
      }
    }

    stage('Pull Docker image') {
      steps {
        script {     
                docker.withRegistry("https://${ECR_REGISTRY}") {
                  def appImage = docker.image("${IMAGE_NAME}").pull()
                }
        }
      }
    }

    
    stage('Deploy to EC2') {
      steps {
        script {  
            withCredentials([sshUserPrivateKey(credentialsId: '67820378-d49b-42aa-b9b3-db19916ccb23', keyFileVariable: 'SSH_PRIVATE_KEY')]) {
              // credentialsid: Jenkins Credentials Id for EC2 credentials 
              // \$SSH_PRIVATE_KEY and \$PATH instead of ${SSH_PRIVATE_KEY} and ${PATH} to access the environment variables ...
              // without Groovy String interpolation
              // added the export PATH=\$PATH:/usr/local/bin command to ensure that the docker-compose executable is ...
              // included in the remote instance's PATH.
              sh """
              scp -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${COMPOSE_FILE} ${EC2_INSTANCE}:~/docker-compose.yml
              scp -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${DOCKERFILE} ${EC2_INSTANCE}:~/Dockerfile
              ssh -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'export PATH=\$PATH:/usr/local/bin && docker-compose -f ~/docker-compose.yml up -d'
              """
            }
        }
      }
    }
  }

          post
    {
        always
        {
            sh "docker rmi -f ${IMAGE_NAME}"
        }
    }
}