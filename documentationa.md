# CI/CD Pipeline for Springboot MongoDB APP Using Jenkins, GitHub, Amazon ECR And Docker-Compose

As per the <a href="https://github.com/earchibong/springboot_project/tree/main#readme">project scope</a> below, with the following steps, a springboot app with mongodb embedded will be built, pushed to ECR, and deployed to EC2 using Jenkins.


<br>

<br>

## Project Steps:
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#set-up-an-ec2-instance-with-docker-and-jenkins-installed">Set up an EC2 instance with Docker and Jenkins installed.</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#create-an-iam-role-for-jenkins-to-access-aws-services">Create an IAM 
user For Jenkins To Access AWS Services</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#create-an-ecr-repository-for-docker-image">Create an ECR repository for Docker image</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#configure-springboot-app-pom-file-to-embed-mongodb">Embed MongoDB in Java Application</a>
- <a href=" ">Create JAR file</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#create-a-dockerfile-for-the-application">Create Dockerfile</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#create-docker-compse-file">Create Docker-Compose File</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentationa.md#create-a-jenkins-job-for-the-cicd-pipeline">Create Jenkins Job For CI/CD Pipeline</a>


<br>

## Set up an EC2 instance with Docker and Jenkins installed.
- spin up two AWS Servers named: `Jenkins` and `App` respectively
- ssh into the `Jenkins` Server

<br>

<img width="1113" alt="ssh_aws_linux" src="https://github.com/earchibong/springboot_project/assets/92983658/2e0fc411-1ef7-4ebb-a62e-2c2ebf707fed">

<br>

- ensure software packages are up to date and port `8080` is open

```

sudo yum update

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

# note 1: install all dependencies on the Jenkins server
# note 2: install docker and docker-compose on both the jenkins and app instances


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
- Amazon EC2

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

<br>


## Create an IAM User for Jenkins to access AWS services
- create a user named `springboot-jenkins` (make a note of access key id and secret access key)
- create a user group named `springboot` from IAM dashbaord
- add your user to the user group
- Give the IAM user group the necessary permissions to access:
  -  ECR: `AmazonEC2ContainerRegistryFullAccess` - `arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess`
  -  EC2: `AmazonEC2FullAccess` - `arn:aws:iam::aws:policy/AmazonEC2FullAccess`

<br>

<br>

```

aws iam create-user --user-name springboot-jenkins
aws iam create-group --group-name springboot
aws iam add-user-to-group --user-name springboot-jenkins --group-name springboot

# verify user is in user group
aws iam get-group --group-name springboot

# attach group policy
aws iam attach-group-policy --group-name springboot --policy-arn <value>

```

<br>


<br>

- Allow the IAM user and Jenkins to execute Docker and Docker Compose commands on the EC2 instance

```

# do this on both the app and jenkins instance
sudo groupadd docker
sudo usermod -aG docker $USER


```

<br>

<br>

- Configure EC2 connection in Jenkins: 

```

manage-jenkins ->  manage nodes and clouds -> configure clouds -> add new a cloud -> Amazon EC2

```

<br>


  - name: `<app server name>`
  - amazon EC2 credentials: `select or add your IAM role`
  - region: `<your instance region>`
  - ec2 private key: <`select add and follow instructions`>
    - Choose the appropriate "Kind" for your private key (e.g., "ssh username with private key").
    - username is "ec2-user-credentials" (using amazon linux instance)
    - copy rsa key for ec2 and store it
    - Save the credentials.
    -  test the connection


<br>

<br>

<img width="1385" alt="clouda" src="https://github.com/earchibong/springboot_project/assets/92983658/3a8c4ff6-b5db-4fbe-8c2c-fdfc9c5fb6f2">
<img width="1387" alt="cloudb" src="https://github.com/earchibong/springboot_project/assets/92983658/48831397-5bae-4774-ba81-f4692685c11f">


<br>

<br>



## Create an ECR repository for Docker image.

```


aws ecr create-repository --repository-name ecs-local --image-scanning-configuration scanOnPush=true --region eu-west-2

```

<br>

<img width="1382" alt="repo_name" src="https://github.com/earchibong/springboot_project/assets/92983658/5bd190a4-2775-4502-a8aa-e48c84b1b993">

<br>

<br>

<img width="1387" alt="ecr_verify" src="https://github.com/earchibong/springboot_project/assets/92983658/c9a0d3a7-242c-44db-b89a-bbd22a12c845">

<br>

<br>

- Link AWS ECR to Jenkins
 - Jenkins Dashboard > Manage Jenkins > Configure System
 - scroll to Docker section
 - Add credentials:
   + Docker label: Docker
   + Docker registry URL: <your ECR url>
   + credentials: add your ECR region and AWS IAM user acess key id and secret access key
   

<br>
    
<img width="1387" alt="docker" src="https://github.com/earchibong/springboot_project/assets/92983658/38f78a6e-cf16-4625-80a9-b1e6752ef3b5">

<br>

<br>



## Configure Springboot App & Create JAR File 

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


In this config. I have overridden the default port 8080 to 27017. Usually we put `host` as `localhost` when we develop apps locally. But we have to put container name as the host here, since we are connecting to `mongodb` container and not the local mongo db on the machine. Make a note of the database name as a `docker-db` container will be created later with it.


<br>

<br>

- in the root directory of the app, create `JAR` File:

```

mvn clean package -DskipTests

```

<br>

<br>

image

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

## Create Docker-Compose File
- Create a file named `docker-compose.yml.template` in the project's root directory and add the following:
```
    
version: '3'
services:
  app:
    image: ${IMAGE_NAME}
    ports:
      - 5000:5000
    container_name: "spring-boot-app"
    environment:
      - MONGO_HOST=mongodb
      - MONGO_PORT=27017
      - MONGO_DB=docker-db
    depends_on:
      - mongodb

  mongodb:
    image: mongo:latest
    container_name: "mongodb"
    ports:
      - 27017:27017 


    
```

<br>

*note: in the app section, we will be using a docker image with a placeholder `${IMAGE_NAME}` that is defined in the jenkinsfile, so we don't need a `build` section. Instead, once the image is built in the jenkinspipeline, it will be pulled from `ECR` to the EC2 instance via the docker-compose file and deployed as the mongodb database.


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

- grant permissions to `jenkins` to gain access to docker
After you've connected to `Jenkins` instance on your terminal, add the following in the command line:

```

sudo groupadd docker
sudo usermod -aG docker $USER
#sudo chmod 777 /var/run/docker.sock
sudo chmod 666 /var/run/docker.sock

```

<br>

## Create Jenkinsfile

- in the root directory of your app file, create a file named `Jenkinsfile`

<br>

Here's an overview of the steps that will be included in the job:
- Check out the source code from the GitHub repository.
- Build the Docker image and tag it with the ECR repository URL.
- Push the Docker image to the ECR repository.
- Deploy the Docker image to EC2 using docker-compose.
    

<br>

```

pipeline {
  environment {
    PROJECT     = 'mongodb-springboot'
    ECR_REGISTRY = "350100602815.dkr.ecr.eu-west-2.amazonaws.com/ecs-local"
    AWS_REGION = "eu-west-2"
    DOCKERFILE = "./Dockerfile"
    //MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
    COMPOSE_FILE = "./docker-compose.yml.template"
    EC2_INSTANCE = "ec2-user@ec2-18-130-4-172.eu-west-2.compute.amazonaws.com"
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

               docker.withRegistry("https://${ECR_REGISTRY}") {
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


```

<br>

<br>

- push changes to git repository
- confirm pipeline build in Jenkins

<br>

<br>

<img width="1387" alt="jenkins_pipeline_deploy" src="https://github.com/earchibong/springboot_project/assets/92983658/9c55cd20-89a4-478d-8c69-e889b5722e71">

<br>
    
<br>
    
<img width="1389" alt="app_5000" src="https://github.com/earchibong/springboot_project/assets/92983658/87768df9-7e4d-4277-a74e-9050a4e82465">

<br>

<br>
    
## Create Reverse Proxy And SSL/TLS Encryption
    
- spin up an EC2 instance and name it `nginx lb`
- open `TCP port 80` for HTTP connections, also open `TCP port 443` for HTTPS 

### Configure NginX Server

- SSH into EC2 instance
- configure DNS records
    - in AWS Route 53, select your hosted zone and domain
    - create an `A` record
    - insert `nginx_lb` ip address as the value
    - create another A record:
    - Enter www in the record name for record type and value enter the same information as above .
    
<br>
    
- Update the instance and Install and enable Nginx at boot:
```
    
 sudo dnf update -y
 sudo dnf install nginx -y
 sudo systemctl start nginx
 sudo systemctl status nginx
 sudo systemctl enable nginx
    
```
<br>
    
<br>
    
<img width="1384" alt="nginx" src="https://github.com/earchibong/springboot_project/assets/92983658/f077346e-918e-4a41-b9c8-e8bdeea626a9">

 <br>
    
 - Make sure you have a domain name pointed at the EC2's ip address.

<br>
    
<br>
    
<img width="1385" alt="dns_verify" src="https://github.com/earchibong/springboot_project/assets/92983658/090ec89b-970e-4607-9d09-d2e4c6070733">

<br>
    
<br>
    
- stop the nginx service 
    
```
    
sudo systemctl stop nginx

```
    
- Install certbot on the EC2 instance
    
```
    
sudo dnf install python3 augeas-libs
sudo python3 -m venv /opt/certbot/
sudo /opt/certbot/bin/pip install --upgrade pip
sudo /opt/certbot/bin/pip install certbot certbot-nginx
sudo ln -s /opt/certbot/bin/certbot /usr/bin/certbot
sudo certbot certonly --nginx
    
```
    
<br>
    
<br>
    
<img width="1386" alt="cert" src="https://github.com/earchibong/springboot_project/assets/92983658/0d7e360c-d0fa-41d7-b865-708ebabd19cd">

<br>
    
<br>
    
    
- Setup Server Block For Nginx

<br>

```
    
sudo nano /etc/nginx/nginx.conf

# update NginX Load Balancer config file with Web Servers’ names defined in /etc/hosts
#insert following configuration into http section

 server {
        listen       80;
        listen       [::]:80;
        server_name  archibong.link;
        location / {
            proxy_pass http://172.31.15.225:5000;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_cache_bypass $http_upgrade;
        }
    }


    
```
    
<br>
    
<br>
 
<img width="1144" alt="etc" src="https://github.com/earchibong/springboot_project/assets/92983658/fae64db7-4851-4772-a70f-352b420aed92">

    
<br>
    
<br>
    

    
<br>
    

 And that's it! We have successfully deployed a Spring Boot application into Docker connected with MongoDB! 


<br>
    
    

