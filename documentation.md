## Jenkins CI/CD Pipeline For A Spring Boot Application With MongoDB And Keycloak On AWS
As per the client's request, the app had to be deployed on AWS. I chose `AWS elastic Beanstalk` because,instead of creating an `AWS EC2` instance and connecting with our application code base manually, everything can be done at once through `Elastic Beanstalk`. 

`Elastic Beanstalk` will create an EC2 instance as a compute resource and `S3` for storage of code(this will be optional in this configuration because the code base is already being sotred in a Github repository). In addition, it will create Security groups, Load balancers, Target groups and etc… Basically it will create a fully fledged scalable environment for the web application.

<br>

## Project Steps:

- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#clone-the-github-repository-containing-the-java-spring-boot-application ">Clone the GitHub repository containing the Java Spring Boot application</a>
- <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md#build-application-jar">build application Jar</a>

<br>

## Clone the GitHub repository containing the Java Spring Boot application.

```

$ git clone https://github.com/SalithaUCSC/SpringBoot-REST-API.git

```

I'm using a sample application from <a href="https://github.com/SalithaUCSC/SpringBoot-REST-API.git">Salitha Chathuranga</a> configured with `Mongo-DB` already embedded.

*Note: When using `AWS Elastic Beanstalk` this configuration of embedding `mongodb` is important because we cannot connect a NoSQL database to `Elastic Beanstalk`. The reason for that is Beanstalk is currently not accepting NoSQL databases for selection. Let me explain that.*

*Usually, after creating a beanstalk application, we can edit the database from “Configuration” option on the UI. But `Elsatic Beanstalk` is currently only offering relational database options. There’s no way to connect a mongodb instance running on AWS to it. So, a simple solution is to connect (embed) a remote mongo database directly from code itself. This bypasses the need to configure beanstalk.*

<br>

- ensure `MongoDB` Maven dependency is included in the `pom` file.
To load an embedded MongoDB with Spring Boot, all that is needed is to add its maven dependency into the pom. The rest will be taken care of. MongoDB binaries will even be downloaded on the fly at build time.

<br>

<img width="1027" alt="mongo_dependency" src="https://github.com/earchibong/springboot_project/assets/92983658/217c8a83-b7f7-4ced-908b-85186492cd5e">

<br>

<br>


- configure a health endpoint for AWS Elastic Beanstalk health checks.
The reason for this endpoint is that the `Elastic Beanstalk` load balancer uses path “/” by default for health checks. If you don’t have that path defined in the controller, the application will keep failing health checks and will show Severe as status in dashboard

```


# Add the Spring Boot Actuator dependency to your pom.xml file:

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>


# Open the application.properties file located in src/main/resources.
# Add the following line to enable the /actuator/health endpoint:

management.endpoint.health.enabled=true

# Add the following lines to customize the health endpoint response to a simple "UP" message:

management.endpoint.health.show-details=never
management.health.status.http-mapping.UP=200


```


<br>

<img width="813" alt="health_end_point" src="https://github.com/earchibong/springboot_project/assets/92983658/72a8520b-0839-42ea-b831-61351d17bd61">


<br>

- create new file `HealthController.java` in `src/main//java` directory and add the following:

```

package com.alexbt.mongodb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

}


```



<br>
   
- Open the `application.properties` file located in `src/main/resources`. Update the `MongoDB URI` to point to your MongoDB instance

```

spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/order-db?retryWrites=true&w=majority
server.port=5000
      
```

<br>

PORT is very important on this config!

Elastic beanstalk by default provides an EC2 instance that runs `Nginx as reverse proxy` and listens to port 80. Nginx by default forwards the request to application running on internal port 5000. Therefore, we need to make sure that our Spring Boot application runs on port 5000. If not, application deployment will fail.

<br>

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

## Build Application Jar
We need to create a JAR file that will be used to create the docker image and subsequently create the application on beanstalk. For that, the below maven command can be used. Then you can find the JAR inside /target folder.

To avoid build failures, I am skipping the tests for now.

- Open the command prompt/terminal and navigate to the project root directory

```

# mvn clean install -DskipTests
mvn package -Dmaven.test.skip.exec


```

<br>

<img width="1470" alt="mvn_package" src="https://github.com/earchibong/springboot_project/assets/92983658/fc0e95a9-c431-4466-a1ee-27eecb816f78">

<br>





