## Deploy Spring Boot MongoDB Application Using AWS Elastic Beanstalk
As per the client's request, the app had to be deployed on AWS. I chose `AWS elastic Beanstalk` because,instead of creating an `AWS EC2` instance and connecting with our application code base manually, everything can be done at once through `Elastic Beanstalk`. 

`Elastic Beanstalk` will create an EC2 instance and connect it to relevant configurations on behalf us.

EC2 — As compute resource
S3 — As storage for source code

In addition, it will create Security groups, Load balancers, Target groups and etc…
Basically it will create a fully fledged scalable environment for the web application.

- **Step One: Clone the GitHub repository containing the Java Spring Boot application.**
I'm using a sample application from <a href="https://github.com/alexbt/sample-spring-boot-data-mongodb-embedded">alexbt</a> configured with `Mongo-DB` already embedded.

when using `AWS Elastic Beanstalk` this configuration is important because we cannot connect a NoSQL database to it. The reason for that is Beanstalk is not accepting any NoSQL databases for selection. Let me explain that.

Usually, after creating a beanstalk application, we can edit the database from “Configuration” option. But `ealstic Beanstalk` is currently only offering relational database options. There’s no way to connect a mongodb instance running on AWS to it. So, a simple solution is to connect (embed) a remote mongo database directly from code itself. This bypasses the need to configure beanstalk.
