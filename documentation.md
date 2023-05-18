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
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-a-jenkins-job-for-the-cicd-pipeline">Create Jenkins Job For CI/CD Pipeline</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-ecs-cluster">Create ECS cluster</a>

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


- install git

```

sudo yum install git

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


## Create an IAM user for Jenkins to access AWS services
- Give the user IAM user the necessary permissions to access ECR and ECS.

I already have a user `terraform jenkins` that was created prerviously so i'm going to add permissions for ECR and ECS. However, here are the steps for creating a user and attaching permissions:

- in `iam` console, create a user group `admin`
- click `add permissions`
- select the permission to add to the user group: 
    - ECS: `AmazonECS_FullAccess`
    - ECR: `EC2InstanceProfileForImageBuilderECRContainerBuilds`
    - EC2: `AmazonEC2ContainerRegistryFullAccess`
- under the `admin` usergroup, create a new user (in my case, `terraform-jenkins`) to make use of the permissions attached to the group

<br>

<img width="1385" alt="iam_user_permissions" src="https://github.com/earchibong/springboot_project/assets/92983658/783a1b9e-4494-4430-bd2a-5ef7611938c6">

<br>

<br>

- attach the following policy to the user: `AmazonECSTaskExecutionRolePolicy`


<br>

<img width="1388" alt="create_cluster" src="https://github.com/earchibong/springboot_project/assets/92983658/f80ebaf0-246c-4c89-ad2f-9f493b6c5dfd">

<br>

## Create ECS Cluster
- Go to the Amazon ECS console.
- Click on "Clusters" in the sidebar and then click `Create Cluster.`
- Enter a name for your cluster, such as `springboot-project`
- Click `Create` to create the cluster.
- skip `task definition`
- service name: `springboot-ecs`
- desired tasks: `1`

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

![image](https://github.com/earchibong/springboot_project/assets/92983658/572b281c-6dd7-4201-a97a-e9dfb5b445e0)

<br>

- Open the `application.properties` file located in src/main/resources. Update the MongoDB URI to point to your MongoDB instance with value: `mongodb://mongo/mydb`

<br>


<img width="1034" alt="application_properties_mongo" src="https://github.com/earchibong/springboot_project/assets/92983658/f2c2843c-b487-417a-a216-56d9eaa69621">


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


## Configure Keycloak Server

-  spin up a new EC2 instance
-  Install & Start Keycloak:
```

# Download the Keycloak distribution package from the Keycloak website. ( I'm using version : 21.1.1)
sudo wget https://github.com/keycloak/keycloak/releases/download/21.1.1/keycloak-21.1.1.tar.gz

# Extract the downloaded package:
sudo tar xf keycloak-21.1.1.tar.gz

# Rename the extracted folder to keycloak for simplicity:
mv keycloak-21.1.1 keycloak

# Generate the SSL certificate (self-signed certificate)
# This version is only for dev purposes

cd keycloak
sudo openssl req -newkey rsa:2048 -nodes \
  -keyout keycloak-server.key.pem -x509 -days 3650 -out keycloak-server.crt.pem


# It will prompt for details like:
Country Name (2 letter code) []:
State or Province Name (full name) []:
Locality Name (eg, city) []:
Organization Name (eg, company) []:
Organizational Unit Name (eg, section) []:
Common Name (eg, fully qualified host name) []:
Email Address []


# Start Keycloak:
# Change to the Keycloak directory:
cd keycloak/bin

# set the environment variables
# note: this is only for development purposes and cannot be used for production
export KEYCLOAK_ADMIN=admin 
export KEYCLOAK_ADMIN_PASSWORD=password
export KC_HOSTNAME_STRICT=false
export KC_HOSTNAME_STRICT_HTTPS=false 
export KC_HTTP_ENABLED=true

# Start the Keycloak server in devlopment mode:
# -Djboss.socket.binding.port-offset=100 ...offsets the default ports by 100 to avoid conflicts with other services running on the instance.
# ./kc.sh start-dev : starts in dev mode
# ./kc.sh start : starts in production mode

sudo -E ./bin/kc.sh start-dev -Djboss.socket.binding.port-offset=100 

# Access the Keycloak Admin Console:
# Open a web browser and navigate to http://<public_ip_address>:8080/admin
# set up the necessary realm and client configurations.


```


<br>

<img width="1388" alt="kc_admin" src="https://github.com/earchibong/springboot_project/assets/92983658/787b5dc1-aea8-4447-9726-c70d49dccb91">

<br>


## Create a Dockerfile For The Application
In the application folder, create a folder named `app` and then create a docker file in the `app` directory. Add the following to the  docker file:

```

FROM openjdk:11-jdk

WORKDIR /app

COPY target/mongodb-springboot.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mongodb-springboot.jar"]

```

<br>

This Dockerfile uses the official `OpenJDK 11 image` as the base, copies the Spring Boot app's JAR file into the container, and sets the entry point to start the app.

<br>

<br>

<img width="1107" alt="dockerfile" src="https://github.com/earchibong/springboot_project/assets/92983658/6a8003a9-014f-423b-9021-f3e793d9b7a3">

<br>

<br>

## Create A Jenkins Job For The CI/CD Pipeline

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
    ECR_REGISTRY = "123456789012.dkr.ecr.us-west-2.amazonaws.com"
    IMAGE_NAME = "myapp"
    IMAGE_TAG = "latest"
    AWS_REGION = "us-west-2"
    ECS_CLUSTER = "my-cluster"
    ECS_SERVICE = "my-service"
    DOCKERFILE = "Dockerfile"
    MAVEN_OPTS = "-Dmaven.repo.local=$WORKSPACE/.m2"
  }
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build') {
      steps {
        sh "mvn -f ${env.WORKSPACE}/pom.xml clean package -DskipTests"
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }
    stage('Build Docker image') {
      steps {
        script {
          docker.withRegistry("https://${ECR_REGISTRY}", 'ecr') {
            def appImage = docker.build("${ECR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}", "--file ${DOCKERFILE} ${env.WORKSPACE}")
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
                    value: "mongodb://mongo/mydb"
                  ],
                  [
                    name: "KEYCLOAK_AUTH_SERVER_URL",
                    value: "http://keycloak:8080/auth"
                  ],
                  [
                    name: "KEYCLOAK_REALM",
                    value: "myrealm"
                  ],
                  [
                    name: "KEYCLOAK_RESOURCE",
                    value: "myapp"
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
sudo chmod 777 /var/run/docker.sock

```

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
     ```
     
    192.30.252.0/22
    185.199.108.0/22
    140.82.112.0/20
    
    ```


<br>

<img width="1388" alt="webhook-1" src="https://github.com/earchibong/springboot_project/assets/92983658/9d75747d-6f98-47e8-ad6c-ecbbb261dcd3">


<br>


<img width="1294" alt="github-webhook-security" src="https://github.com/earchibong/springboot_project/assets/92983658/2fb5a18b-3ed3-488e-97c1-02dd3e728f24">

<br>

- Pipeline: `Pipeline script from scm`
    - scm: `git`
    - enter repository url and github credentials
    - branch : reposority branch your project is stored on..in my case `*/main`

- Script Path: `Jenkinsfile`



