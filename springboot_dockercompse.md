# CI/CD Pipeline for Springboot MongoDB APP Using Jenkins, GitHub, Amazon ECR And Docker-Compose

As per the <a href="https://github.com/earchibong/springboot_project/tree/main#readme">project scope</a>, with the following steps, a springboot with mongodb embedded will be built, pushed to ECR, and deployed to EC2 using Jenkins.


<br>

<img width="756" alt="upwork_cicd" src="https://github.com/earchibong/springboot_project/assets/92983658/58b9d072-cdc5-41a9-8a82-131838703003">

<br>

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
sudo systemctl enable docker.service
sudo service docker start

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


- install git, maven, aws cli, docker compose

```

sudo yum install git
sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

curl "https://d1vvhvl2y92vvt.cloudfront.net/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

sudo wget https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)
sudo mv docker-compose-$(uname -s)-$(uname -m) /usr/local/bin/docker-compose
sudo chmod -v +x /usr/local/bin/docker-compose
docker-compose version


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

- Create credentials to allow Jenkins to ssh into EC2
 - Create a Jenkins credential for the private key: Go to `"Manage Jenkins" > "Manage Credentials" > "Jenkins" > "Global credentials" (or "System" depending on Jenkins version) and click on "Add Credentials".`
  + Choose the appropriate "Kind" for your private key (e.g., "ssh username with private key").
  + Provide a meaningful "ID" and "Description".
  + username is "ec2-user" as i', using amazon linux instance
  + copy rsa key for ec2 and store it
  
  + Save the credentials.

<br>

<br>

imge

<br>

<br>



## Create an IAM Role for Jenkins to access AWS services
- Give the IAM role, the necessary permissions to access ECR and ECS.

I already have a role `ECR-Jenkins` that was created prerviously so i'm going to add permissions for ECR and ECS. However, here are the steps for creating a user and attaching permissions:

- in `iam` console, create a role (name it whatever you want)
- click `add permissions`
 - select the permission + Policies to add to the role: 
    - ECR: `AmazonEC2ContainerRegistryFullAccess`
    - EC2: `AmazonEC2FullAccess`

<br>

<img width="1390" alt="ecr_iam" src="https://github.com/earchibong/springboot_project/assets/92983658/55fc5b46-e2f1-4b0a-965a-b0ba7acda8a1">

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

- Allow the IAM role and Jenkins to execute Docker and Docker Compose commands on the EC2 instance

```

sudo groupadd docker
sudo usermod -aG docker $USER
sudo usermod -aG docker <IAM_ROLE>

```

<br>

<br>


- Link AWS ECR to Jenkins
 - Jenkins Dashboard > Manage Jenkins > Configure System
 - scroll to Docker section
 - Add credentials:
   + Docker label: Docker
   + Docker registry URL: <your ECR url>
   + credentials: add your ECR region and AWS IAM role
   

<br>
    
<img width="1387" alt="docker" src="https://github.com/earchibong/springboot_project/assets/92983658/38f78a6e-cf16-4625-80a9-b1e6752ef3b5">

<br>

<br>



## Create an ECR repository for Docker image.
ECR doesn't usually include image names when tagging, only the image tag like below...

 ```
 
 docker push aws_account_id.dkr.ecr.us-west-2.amazonaws.com/my-repository:tag

 ```

 .... so, to make it easy tag and push in jenkinspipeline, make sure your repository name is your docker image name.

```


aws ecr create-repository --repository-name mongodb-springboot --image-scanning-configuration scanOnPush=true --region eu-west-2

```

<br>

<img width="1382" alt="repo_name" src="https://github.com/earchibong/springboot_project/assets/92983658/5bd190a4-2775-4502-a8aa-e48c84b1b993">

<br>

<br>

<img width="1387" alt="ecr_verify" src="https://github.com/earchibong/springboot_project/assets/92983658/c9a0d3a7-242c-44db-b89a-bbd22a12c845">

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

- Open the `application.properties` file located in src/main/resources. Update the MongoDB URI to point to your MongoDB instance with value: `mongodb://mongodb:27017/<your-database-name>`

<br>

<img width="1027" alt="spring_boot_db" src="https://github.com/earchibong/springboot_project/assets/92983658/f41e6da3-7d2f-4500-ad64-689000e8e350">


<br>

<br>


In this config. I have overridden the default port 8080 to 27017. Usually we put `host` as `localhost` when we develop apps locally. But we have to put container name as the host here, since we are connecting to `mongo db docker` container and not the local mongo db on the machine. Make a note of the database name as a `mongodb docker` container will be created later with it.


<br>

<br>

## Create a Dockerfile For The Application
In the application folder, create a folder named `app` and then create a docker file and add the following to the  docker file:

```

    
FROM adoptopenjdk:11-jre-hotspot

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the container
COPY target/mongodb-springboot.jar app.jar

# Expose the port on which your Spring Boot application listens
EXPOSE 8080

# Set the entry point command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


```

<br>

This Dockerfile uses the official `adoptopenjdk 11 image` as the base, copies the Spring Boot app's JAR file into the container, and sets the entry point to start the app.

<br>

<br>

<img width="811" alt="dockerfile_1a" src="https://github.com/earchibong/springboot_project/assets/92983658/cb42531a-0f15-4450-9c3a-de759dadcb1f">

<br>

<br>

## Create Docker-Compse File
- Create a file named `docker-compose.yml` in the project's root directory and add the following:
```
    
version: '3'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      container_name: "springboot-app"
    ports:
      - 8080:8080
    environment:
      - MONGO_HOST=mongodb
      - MONGO_PORT=27017
      - MONGO_DB=springboot-db
    depends_on:
      - mongodb

  mongodb:
    image: mongo:latest
    container_name: "mongo-db"
    ports:
      - 27017:27017


    
```

<br>
    
<br>
    
<img width="822" alt="docker_compose" src="https://github.com/earchibong/springboot_project/assets/92983658/7e685490-a6c7-4140-be35-17190dfc1aec">

<br>
    
<br>
    

    
## Create A Jenkins Job For The CI/CD Pipeline

- Configure Jenkins pipeline: Go to the Jenkins Dashboard and create new job
    - Enter the name of the job and select the type of job you wish to run on Jenkins.
    - Select the `pipeline` option to to automate the tasks in your pipeline.

<br>

<img width="1391" alt="pipeline" src="https://github.com/earchibong/springboot_project/assets/92983658/e1f63aab-fa96-40b3-a6f8-8020f7340ff5">

<br>

<br>

- Build triggers: `GitHub hook trigger for GITScm polling`
    - configure <a href="https://docs.github.com/en/webhooks-and-events/webhooks/creating-webhooks">webhook on Github here</a>
    - when creating webhook on github select:
     - payload url should be `http` not `https`
     - select `x-www-form-urlencoded ` content type
     - disable `ssl` for now as there is no certificate attached to jenkins
     - ensure that ports `22`, `80` and `443` are open in your jenkins security group

<br>

<br>

<img width="1388" alt="webhook-1" src="https://github.com/earchibong/springboot_project/assets/92983658/9d75747d-6f98-47e8-ad6c-ecbbb261dcd3">


<br>

<br>

- add the following `github webhook ip` over port `8080` in your ec2 security group settings:

<br>

```
     
    192.30.252.0/22
    185.199.108.0/22
    140.82.112.0/20


```

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

<br>

```

# configuration path:
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

<br>

- in the root directory of your app file, create a file named `Jenkinsfile`

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
    PROJECT     = 'mongodb-springboot'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/mongodb-springboot"
    IMAGE_NAME = "mongodb-springboot"
    IMAGE_TAG = "latest"
    AWS_REGION = "eu-west-2"
    DOCKERFILE = "Dockerfile"
    MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
    COMPOSE_FILE='docker-compose.yml'
    EC2_INSTANCE = 'ec2-user@ec2-instance-ip'
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
              sh "docker pull ${ECR_REGISTRY}:${IMAGE_TAG}"
              sh "scp -i ${credentials('private-key-credential-id-from-jenkins')} -o StrictHostKeyChecking=no ${COMPOSE_FILE} ${EC2_INSTANCE}:~/docker-compose.yml"
              sh "ssh -i ${credentials('private-key-credential-id-from-jenkins')} -o StrictHostKeyChecking=no ${EC2_INSTANCE} 'docker-compose -f ~/docker-compose.yml up -d'"
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
sudo usermod -aG docker <IAM_ROLE>
#sudo chmod 777 /var/run/docker.sock
sudo chmod 666 /var/run/docker.sock

```

<br>

- push changes to git repository
- confirm pipeline build in Jenkins

--
testing 
