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
          docker.withRegistry("https://${ECR_REGISTRY}", 'c70c865e-ffbe-4d45-94f4-0e443d88cdec') {
            def appImage = docker.image("${IMAGE_NAME}:${IMAGE_TAG}")
            appImage.build("-f ${DOCKERFILE} ${env.WORKSPACE}")
            appImage.push()
      }
    }
  }
}


    
    stage('Deploy') {
      steps {
        script {
          def ecsParams = [
            containerDefinitions: [
              [
                name: "mongodb-springboot",
                image: "${ECR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}",
                essential: true,
                portMappings: [
                  [
                    containerPort: 8080,
                    hostPort: 0,
                    protocol: "tcp"
                  ]
                ],
                environment: [
                  [
                    name: "SPRING_DATA_MONGODB_URI",
                    value: "mongodb+srv://darey:darey@cluster0.ixif8fy.mongodb.net/?retryWrites=true&w=majority"
                  ]
                ]
              ]
            ],
            taskRoleArn: "arn:aws:iam::350100602815:role/ecsTaskExecutionRole",
            family: "springboot_task_family",
            networkMode: "awsvpc",
            requiresCompatibilities: ["FARGATE"],
            cpu: "256",
            memory: "512",
            executionRoleArn: "arn:aws:iam::350100602815:role/ecsTaskExecutionRole",
            networkConfiguration: [
              awsvpcConfiguration: [
                assignPublicIp: "ENABLED",
                subnets: ["subnet-0d0d30fa5368a334b"],
                securityGroups: ["sg-0d650c3fde5367c85"]
              ]
            ]
          ]

          ecsParams.containerDefinitionsJson = ecsParams.containerDefinitions as String
          ecsParams.networkConfigurationJson = ecsParams.networkConfiguration as String

          def taskDefinition = null
          withCredentials([[
              $class: 'AmazonWebServicesCredentialsBinding',
              accessKeyVariable: 'AWS_ACCESS_KEY_ID',
              credentialsId: 'aws-credentials',
              secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
            ]]) {
            sh """
              aws configure set aws_access_key_id ${env.AWS_ACCESS_KEY_ID}
              aws configure set aws_secret_access_key ${env.AWS_SECRET_ACCESS_KEY}
              aws configure set default.region ${env.AWS_REGION}

              aws ecs register-task-definition --cli-input-json '${ecsParams as String}'
              taskDefinition=\$(aws ecs list-task-definitions --family-prefix springboot_task_family | jq -r '.taskDefinitionArns[0]')

              aws ecs update-service --cluster ${ECS_CLUSTER} --service ${ECS_SERVICE} --task-definition \${taskDefinition}
            """
          }
        }
      }
    }
  }
}