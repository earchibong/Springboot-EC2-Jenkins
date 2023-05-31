pipeline {
  environment {
    PROJECT     = 'mongodb-springboot'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
    AWS_REGION = "eu-west-2"
    DOCKERFILE = "./Dockerfile"
    //MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
    COMPOSE_FILE = "./docker-compose.yml.template"
    EC2_INSTANCE = "ec2-user@ec2-35-178-232-60.eu-west-2.compute.amazonaws.com"
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
    
    //stage('Build Jar file') {
      //steps {
        //sh "mvn -f ${env.WORKSPACE}/pom.xml clean package -DskipTests"
        //archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      //}
    //}
    
    stage('Build Docker image') {
      steps {
        script {
               withCredentials([
                [
                  $class: 'AmazonWebServicesCredentialsBinding',
                  accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                  secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                  credentialsId: 'be528753-f3b5-4a0b-af49-7ff229fff5d1'
                ]
              ]) {
               
               sh """
               aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
               docker build --tag ${IMAGE_NAME} --file ${DOCKERFILE} ${env.WORKSPACE}
               """

               docker.withRegistry("https://${ECR_REGISTRY}", "${AWS_REGION}") {
                docker.image("${IMAGE_NAME}").push() 
                }
              }
        }
      }
    }

    stage('Deploy to EC2') {
      steps {
        script {  
            withCredentials([
              sshUserPrivateKey(credentialsId: '67820378-d49b-42aa-b9b3-db19916ccb23', keyFileVariable: 'SSH_PRIVATE_KEY'), 
                [
                  $class: 'AmazonWebServicesCredentialsBinding',
                  accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                  secretKeyVariable: 'AWS_SECRET_ACCESS_KEY',
                  credentialsId: 'be528753-f3b5-4a0b-af49-7ff229fff5d1'
                ]
              ]) {
              
              // using `\$SSH_PRIVATE_KEY` and `\$PATH` instead of `${SSH_PRIVATE_KEY}` and `${PATH}` to access the environment variables without Groovy String interpolation
              // added the `export PATH=\$PATH:/usr/local/bin` command to ensure that the docker-compose executable is included in the remote instance's PATH.
              // Deploy the Docker image on the EC2 instance
              sh """

              ssh -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'export PATH=\$PATH:/usr/local/bin && aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'
              scp -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${COMPOSE_FILE} ${EC2_INSTANCE}:~/docker-compose.yml.template
              ssh -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'mv ~/docker-compose.yml.template ~/docker-compose.yml'
              ssh -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'export PATH=\$PATH:/usr/local/bin && sed -i "s|{{IMAGE_NAME}}|${IMAGE_NAME}|g" ~/docker-compose.yml'
              ssh -i \$SSH_PRIVATE_KEY -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'export PATH=\$PATH:/usr/local/bin && IMAGE_NAME=${IMAGE_NAME} docker-compose -f ~/docker-compose.yml up -d'
              
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