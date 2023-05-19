# Continuous Delivery Pipeline for Amazon ECS Using Jenkins, GitHub, and Amazon ECR

As per the project scope, the app had to be deployed on AWS. I chose AWS Elastic Container Service because of the flexibility and infrastructure control it offers.

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/5676b455-cb16-43e1-8492-b7e86f4fe100)

<br>

## Project Steps:
- <a href=" ">Set up an EC2 instance with Docker and Jenkins installed.</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-an-iam-user-for-jenkins-to-access-aws-services">Create an IAM 
user For Jenkins To Access AWS Services</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-an-ecr-repository-for-docker-image">Create an ECR repository for Docker image</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#clone-the-github-repository-containing-the-java-spring-boot-application">Embed MongoDB in Java Application</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-ecs-cluster">Create ECS cluster</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-an-ecr-repository-for-docker-image">Create ECR Repository</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#configure-springboot-app-pom-file-to-embed-mongodb">Configure Springboot App for MongoDB</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-a-dockerfile-for-the-application">Create Dockerfile For the Application</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-a-jenkins-job-for-the-cicd-pipeline">Create Jenkins Job For CI/CD Pipeline</a>

<br>

## Set up an EC2 instance with Docker and Jenkins installed.
- ssh into AWS EC2 Server

<br>

<img width="1113" alt="ssh_aws_linux" src="https://github.com/earchibong/springboot_project/assets/92983658/2e0fc411-1ef7-4ebb-a62e-2c2ebf707fed">

<br>

- ensure software packages are up to date and port `8080` is open

```

sudo yum update –y

```

<br>

- install and run docker

```

sudo yum install docker
sudo service docker start
sudo systemctl enable docker.service

# verify docker service
sudo systemctl status docker.service

# get docker info
sudo docker info

```

<br>

<img width="1470" alt="docker install" src="https://github.com/earchibong/springboot_project/assets/92983658/b349f315-aa07-4208-b747-2a22741f79b2">


<br>


<img width="1253" alt="docker_start" src="https://github.com/earchibong/springboot_project/assets/92983658/97f9327d-68eb-421f-a8de-8c0fa80ab849">

<br>


<img width="1381" alt="docker_verify" src="https://github.com/earchibong/springboot_project/assets/92983658/d0d867a0-f797-4fba-92b3-1c4fe2af1285">

<br>


*note: control docker service with the following commands*

```

sudo systemctl start docker.service #<-- start the service
sudo systemctl stop docker.service #<-- stop the service
sudo systemctl restart docker.service #<-- restart the service
sudo systemctl status docker.service #<-- get the service status

```

<br>


- install git, maven and aws cli

```

sudo yum install git
sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

curl "https://d1vvhvl2y92vvt.cloudfront.net/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install


```

<br>


- install and run jenkins

```

# Add the Jenkins repo 
sudo wget -O /etc/yum.repos.d/jenkins.repo \
    https://pkg.jenkins.io/redhat-stable/jenkins.repo
    
# Import a key file from Jenkins-CI to enable installation from the package
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
sudo yum upgrade

# Install Java 
sudo dnf install java-11-amazon-corretto -y

# Install Jenkins:
sudo yum install jenkins -y

# Enable the Jenkins service to start at boot:
sudo systemctl enable jenkins

# Start Jenkins as a service:
sudo systemctl start jenkins

# check the status of the Jenkins service
sudo systemctl status jenkins

```

<br>

<img width="1381" alt="jenkins_stauts" src="https://github.com/earchibong/springboot_project/assets/92983658/acbec69e-0503-44ee-a9c1-30150a191e19">

<br>

<br>

- configure jenkins

```

# access Jenkins through its management interface:
http://<your_server_public_DNS>:8080

# enter the password found in **/var/lib/jenkins/secrets/initialAdminPassword.**
sudo cat /var/lib/jenkins/secrets/initialAdminPassword

# Install suggested plugins
# Create First Admin User
Follow instruction on jenkins management interface

# install docker plugins to build and push the Docker image to Docker Hub and ECR
# install the following plugins:
- CloudBees Docker Build and Publish, 
- Amazon ECR, 
- Docker pipeline 
- Blue ocean
- pipeline maven integration

```

<br>

<img width="1388" alt="jenkins_plugins" src="https://github.com/earchibong/springboot_project/assets/92983658/60f324e4-a7d2-4f1c-bc5e-d93fcf823494">

<br>


<br>


- link github repo to jenkins
    - Select `Credentials` in the right hand sidebar.
    - Select `Global credentials (unrestricted)`
    - Add credentials.
     - kind: `username and password`
     - username: your github user name.
     - password: enter <a href="https://docs.github.com/en/enterprise-server@3.4/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token">github personal access token</a>
    
<br>

<br>

<img width="1387" alt="jenkins_github_credentials" src="https://github.com/earchibong/springboot_project/assets/92983658/880ef23e-3abe-4897-8e60-12d3af2284bc">


<br>

<br>

- AWS to Jenkins
 - Select `Credentials` in the right hand sidebar.
    - Select `Global credentials (unrestricted)` for system
    - Add credentials.
     - kind: `AWS credentials`
     - secret id: your AWS username.
     - secret access key


<br>

<br>


## Create an IAM Role for Jenkins to access AWS services
- Give the IAM role, the necessary permissions to access ECR and ECS.

I already have a role `ECR-Jenkins` that was created prerviously so i'm going to add permissions for ECR and ECS. However, here are the steps for creating a user and attaching permissions:

- in `iam` console, create a role (name it whatever you want)
- click `add permissions`
 - select the permission + Policies to add to the role: 
    - ECS: `AmazonECS_FullAccess`
    - ECR: `AmazonEC2ContainerRegistryFullAccess`
    - EC2: `AmazonEC2FullAccess`
    - ECS: `AmazonECSTaskExecutionRolePolicy`

<br>

<img width="1390" alt="ecr-jenkins" src="https://github.com/earchibong/springboot_project/assets/92983658/8c4ce1d5-40bb-4f48-a5c9-9664b74949a6">

<br>

<br>

- in the Jenkins instance, modify the IAM role for the instance: select `Actions > Security > Modify IAM Role`

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/4efbfc4b-7c0b-4666-a47f-68bcc6c16177)

<br>

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/7a7b8c33-85f2-4728-8d41-c442086e1db6)

<br>


<br>

## Create ECS Cluster
- Go to the Amazon ECS console.
- Click on "Clusters" in the sidebar and then click `Create Cluster.`
- Enter a name for your cluster, such as `springboot-project`
- Click `Create` to create the cluster.


<br>

<img width="1354" alt="ecs_cluster" src="https://github.com/earchibong/springboot_project/assets/92983658/e7888696-da81-4a0e-a587-02aabeda2216">

<br>

<br>


## Create an ECR repository for Docker image.
```

aws ecr create-repository --repository-name ecs-local --image-scanning-configuration scanOnPush=true --region eu-west-2

```

<br>

<img width="1388" alt="ecr_repo" src="https://github.com/earchibong/springboot_project/assets/92983658/6a02bd66-ee92-4b3e-a990-79818678e1d6">

<br>

<br>

<img width="1385" alt="ecr_verify" src="https://github.com/earchibong/springboot_project/assets/92983658/e4045010-22f7-4a54-8bbb-ba92db747b3b">

<br>

<br>

## Configure Springboot App POM file to embed MongoDB

- clone the app repository
```

git clone https://github.com/alexturcot/sample-spring-boot-data-mongodb-embedded.git

```

I'm using a sample application from <a href="https://github.com/alexbt/sample-spring-boot-data-mongodb-embedded">alexbt</a> configured with Mongo-DB already embedded. However,we will still ensure it mongodb is embedded.

To load an embedded MongoDB with Spring Boot, all that is needed is to add its maven dependency into the pom. Open the `pom.xml` file and confirm that the mongodb maven dependency is included in the file

<br>

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/572b281c-6dd7-4201-a97a-e9dfb5b445e0)

<br>

<br>

- Open the `application.properties` file located in src/main/resources. Update the MongoDB URI to point to your MongoDB instance with value: `mongodb+srv://<username>:<password>@cluster0.ixif8fy.mongodb.net/?retryWrites=true&w=majority`

<br>


<img width="1023" alt="mongo_rui" src="https://github.com/earchibong/springboot_project/assets/92983658/69429b96-9c7c-4a0a-87c1-8baba6576266">


<br>

<br>


In this config. i'm using the mongodb service provider `Mongo Atlas` so to get the `mongodb uri`...

**To get the mongoDb uri**
- sign up to <a href="https://www.mongodb.com/cloud/atlas/register"> `mongodb`</a>
- deploy your database

<br>

<img width="1387" alt="deploy_cluster_1a" src="https://github.com/earchibong/springboot_project/assets/92983658/0b1ba567-37ff-4c4b-bc16-5a315e2c9fdc">

<br>

<br>

<img width="1374" alt="create_cluster_1b" src="https://github.com/earchibong/springboot_project/assets/92983658/745d398c-bb44-4d9a-9613-4cb35f3a998b">

<br>

<br>

<img width="1390" alt="create_cluster_1c" src="https://github.com/earchibong/springboot_project/assets/92983658/935d2857-0ecc-4908-89df-152cc3c6c918">



<br>

- click the connect button to explore connection options. for this project, we will use the `drivers` option. So copy the connection string provided at add to the application layer in the app code base.

<br>

<br>

<img width="1383" alt="mongo_connect_1a" src="https://github.com/earchibong/springboot_project/assets/92983658/7891619d-7b66-4767-a0e3-bd6fce765d19">

<br>

<br>

<img width="1390" alt="mongo_connect_1b" src="https://github.com/earchibong/springboot_project/assets/92983658/6d1a9cb6-d707-4eaa-9052-ca61c7fbd780">

<br>

<br>

<img width="823" alt="mongo_uri" src="https://github.com/earchibong/springboot_project/assets/92983658/eb233ffb-5eda-4852-95b4-4138d4f671d8">

<br>

<br>

<br>

## Create a Dockerfile For The Application
In the application folder, create a folder named `app` and then create a docker file in the `app` directory. Add the following to the  docker file:

```

FROM openjdk:11-jdk

WORKDIR /app

COPY target/mongodb-springboot-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mongodb-springboot-1.0.0-SNAPSHOT.jar"]

```

<br>

This Dockerfile uses the official `OpenJDK 11 image` as the base, copies the Spring Boot app's JAR file into the container, and sets the entry point to start the app.

<br>

<br>

<img width="1107" alt="dockerfile" src="https://github.com/earchibong/springboot_project/assets/92983658/6a8003a9-014f-423b-9021-f3e793d9b7a3">

<br>

<br>

## Create A Jenkins Job For The CI/CD Pipeline

- Configure Jenkins pipeline: Go to the Jenkins Dashboard and create new job
    - Enter the name of the job and select the type of job you wish to run on Jenkins.
    - Select the `pipeline` option to to automate the tasks in your pipeline.

<br>

<img width="1391" alt="pipeline" src="https://github.com/earchibong/springboot_project/assets/92983658/e1f63aab-fa96-40b3-a6f8-8020f7340ff5">

<br>


- Build triggers: `GitHub hook trigger for GITScm polling`
    - configure <a href="https://docs.github.com/en/webhooks-and-events/webhooks/creating-webhooks">webhook on Github here</a>
    - when creating webhook on github select:
     - payload url should be `http` not `https`
     - select `x-www-form-urlencoded ` content type
     - disable `ssl` for now as there is no certificate attached to jenkins
     - ensure that ports `22`, `80` and `443` are open in your jenkins security group
     - add the following `github webhook ip` over port `8080` in your ec2 security group settings:

<br>

     ```
     
    192.30.252.0/22
    185.199.108.0/22
    140.82.112.0/20
    
    ```


<br>

<img width="1388" alt="webhook-1" src="https://github.com/earchibong/springboot_project/assets/92983658/9d75747d-6f98-47e8-ad6c-ecbbb261dcd3">


<br>

<br>


<img width="1294" alt="github-webhook-security" src="https://github.com/earchibong/springboot_project/assets/92983658/2fb5a18b-3ed3-488e-97c1-02dd3e728f24">

<br>

<br>

- Pipeline: `Pipeline script from scm`
    - scm: `git`
    - enter repository url and github credentials
    - branch : reposority branch your project is stored on..in my case `*/main`

- Script Path: `Jenkinsfile`
- pipeline syntax:
    - sample step : checkout from version control,
    - SCM : git,
    - enter repository and git credentials,
    - enter branches to build - main ...then generate the pipeline script.
    - use pipeline script to update jenkins file stage "checkout"


<br>

<br>

- configure Jenkins with github server

```
# path:
Jenkins Dashboard > Manage Jenkins > Configure System
scroll to Github section
Add github server:
 - name:github
 - credentials:
   - type: secret text
   - secret: github personal access token
   - tick manage hooks

test connection to ensure it all works on jenkins and verify webhook on github


```

<br>

<br>

<img width="1387" alt="github_server" src="https://github.com/earchibong/springboot_project/assets/92983658/a59de66c-ea0b-4364-920c-df95112a4663">


<br>

<br>

- in the root directory of your app file, create a file named `Jenkinsfile`
- create the Jenkinsfile as follows:

<br>

Here's an overview of the steps that will be included in the job:
    - Check out the source code from the GitHub repository.
    - Build the Spring Boot app with Maven or Gradle.
    - Build the Docker image and tag it with the ECR repository URL.
    - Push the Docker image to the ECR repository.
    - Deploy the Docker image to ECS using a task definition and a service.
    

<br>

```

pipeline {
  environment {
    PROJECT     = 'springboot-ecs'
    ECR_REGISTRY = "<your aws account>.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
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
      checkout scmGit(
        branches: [[name: '*/main'],
        extensions: [], 
        userRemoteConfigs: [[credentialsId: 'e1868d62-3cd4-44da-aba1-a24e2183d6e3', url: 'https://github.com/earchibong/springboot_project.git']]
        )
        
      }
    }
    
    stage('Build Jar image') {
      steps {
        sh "mvn -f ${env.WORKSPACE}/pom.xml clean package -DskipTests"
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
    
    stage('Build Docker image') {
      steps {
        echo 'Build Dockerfile....'
        script {
          sh("eval \$(aws ecr get-login-password --no-include-email --region eu-west-2 | sed 's|https://||')")
          sh "docker build --tag ${IMAGE_NAME} --file ${DOCKERFILE} ${env.WORKSPACE}"
          docker.withRegistry("https://${ECR_REGISTRY}") {
            docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
          }
        }
      }
    }
    
    stage('Deploy App') {
      steps {
        script {
          def ecsParams = [
            containerDefinitions: [
              [
                name: "myapp",
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
                    value: "<your mongodb url>"
                  ]
                ]
              ]
            ],
            taskRoleArn: "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
            family: "my-task-family",
            networkMode: "awsvpc",
            requiresCompatibilities: ["FARGATE"],
            cpu: "256",
            memory: "512",
            executionRoleArn: "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
            networkConfiguration: [
              awsvpcConfiguration: [
                assignPublicIp: "ENABLED",
                subnets: ["subnet-12345678"],
                securityGroups: ["sg-12345678"]
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
              taskDefinition=\$(aws ecs list-task-definitions --family-prefix my-task-family | jq -r '.taskDefinitionArns[0]')

              aws ecs update-service --cluster ${ECS_CLUSTER} --service ${ECS_SERVICE} --task-definition \${taskDefinition}
            """
          }
        }
      }
    }
  }
}



```

<br>

<br>

- grant permissions to `jenkins` to gain access to docker
After you've connected to `Jenkins` instance on your terminal, add the following in the command line:

```

sudo groupadd docker
sudo usermod -aG docker $USER
#sudo chmod 777 /var/run/docker.sock
sudo chmod 666 /var/run/docker.sock

```

<br>

- push changes to git repository
- confirm pipeline build in Jenkins

