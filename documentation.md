## Jenkins CI/CD Pipeline For A Spring Boot Application With MongoDB And Keycloak On AWS
As per the client's request, the app had to be deployed on AWS. I chose `AWS elastic Beanstalk` because,instead of creating an `AWS EC2` instance and connecting with our application code base manually, everything can be done at once through `Elastic Beanstalk`. 

`Elastic Beanstalk` will create an EC2 instance as a compute resource and `S3` for storage of code(this will be optional in this configuration because the code base is already being sotred in a Github repository). In addition, it will create Security groups, Load balancers, Target groups and etc… Basically it will create a fully fledged scalable environment for the web application.

<br>

## Project Steps:

- <a href=" ">Clone the GitHub repository containing the Java Spring Boot application</a>
- 

<br>

- **Step One: Clone the GitHub repository containing the Java Spring Boot application.**

```

$ git clone https://github.com/alexturcot/sample-spring-boot-data-mongodb-embedded.git

```
I'm using a sample application from <a href="https://github.com/alexbt/sample-spring-boot-data-mongodb-embedded">alexbt</a> configured with `Mongo-DB` already embedded.

*Note: When using `AWS Elastic Beanstalk` this configuration of embedding `mongodb` is important because we cannot connect a NoSQL database to `Elastic Beanstalk`. The reason for that is Beanstalk is currently not accepting NoSQL databases for selection. Let me explain that.*

*Usually, after creating a beanstalk application, we can edit the database from “Configuration” option on the UI. But `Elsatic Beanstalk` is currently only offering relational database options. There’s no way to connect a mongodb instance running on AWS to it. So, a simple solution is to connect (embed) a remote mongo database directly from code itself. This bypasses the need to configure beanstalk.*

<br>

- ensure `MongoDB` Maven dependency is included in the `pom` file.
To load an embedded MongoDB with Spring Boot, all that is needed is to add its maven dependency into the pom. The rest will be taken care of. MongoDB binaries will even be downloaded on the fly at build time.

<br>

<img width="1027" alt="pom" src="https://github.com/earchibong/springboot_project/assets/92983658/bfc0a048-03a1-4186-b1d6-9c7bd6ee3b6d">


<br>


- ensure to update the health controller as follows:

```

@RestController
public class HealthController {
    @GetMapping("/")
    public String ping() {
        return "Hello & Welcome to Order Service !!!";
    }
}

```

*note: The reason for this endpoint is that `Elastic Beanstalk` load balancer uses path “/” by default for health checks. If you don’t have that path defined in the controller, the application will keep failing health checks and will show Severe as status in dashboard.*

<br>

- Connect MongoDB using AtlasDB. Update the `application` layer as follows:

```

server:
  port: 5000
spring:
  data:
    mongodb:
      uri: mongodb+srv://<username>:<password>@<cluster>.mongodb.net/order-db?retryWrites=true&w=majority
      
```

<br>

PORT is very important on this config!

Elastic beanstalk by default provides an EC2 instance that runs `Nginx as reverse proxy` and listens to port 80. Nginx by default forwards the request to application running on internal port 5000. Therefore, we need to make sure that our Spring Boot application runs on port 5000. If not, application deployment will fail.

<br>

To get the mongoDb uri

