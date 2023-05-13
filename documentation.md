# Continuous Delivery Pipeline for Amazon ECS Using Jenkins, GitHub, and Amazon ECR

As per the project scope, the app had to be deployed on AWS. I chose AWS Elastic Container Service because of the flexibility and infrastructure control it offers.

<br>

![image](https://github.com/earchibong/springboot_project/assets/92983658/5676b455-cb16-43e1-8492-b7e86f4fe100)

<br>

## Project Steps:
- <a href=" ">Set up an EC2 instance with Docker and Jenkins installed.</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-an-iam-user-for-jenkins-to-access-aws-services">Create an IAM user for Jenkins to access AWS services</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#create-an-ecr-repository-for-docker-image">Create an ECR repository for Docker image</a>

<br>

## Set up an EC2 instance with Docker and Jenkins installed.
- ssh into AWS EC2 Server

<br>

<img width="1113" alt="ssh_aws_linux" src="https://github.com/earchibong/springboot_project/assets/92983658/2e0fc411-1ef7-4ebb-a62e-2c2ebf707fed">

<br>

- software packages are up to date

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
- Github integration


```

<br>

<img width="1388" alt="jenkins_plugins" src="https://github.com/earchibong/springboot_project/assets/92983658/60f324e4-a7d2-4f1c-bc5e-d93fcf823494">

<br>

<br>

- link github repo to jenkins
    - Select "Credentials" in the right hand sidebar.
    - Select "Global credentials (unrestricted)" Note: these credentials are only available to projects in your folder.
    - Add credentials.
    - kind: "username and password"
    - username: your github user name.
    - password: enter github password

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
- under the `admin` usergroup, create a new user (in my case, `terraform-jenkins`) to make use of the permissions attached to the group

<br>

<img width="1385" alt="iam_user_permissions" src="https://github.com/earchibong/springboot_project/assets/92983658/783a1b9e-4494-4430-bd2a-5ef7611938c6">

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

## Clone the GitHub repository containing the Java Spring Boot application.
```

git clone https://github.com/ramya1703/mongodb-springboot/tree/main

```

I'm using a sample application from <a href="https://github.com/ramya1703/mongodb-springboot/tree/main
">Ramya1703</a> configured with Mongo-DB already embedded.

