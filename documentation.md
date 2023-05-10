## Deploy Spring Boot MongoDB Application Using AWS Elastic Beanstalk
As per the client's request, the app had to be deployed on AWS. I chose `AWS elastic Beanstalk` because,instead of creating an `AWS EC2` instance and connecting with our application code base manually, everything can be done at once through `Elastic Beanstalk`. 

`Elastic Beanstalk` will create an EC2 instance and connect it to relevant configurations on behalf us.

EC2 — As compute resource
S3 — As storage for source code

In addition, it will create Security groups, Load balancers, Target groups and etc…
Basically it will create a fully fledged scalable environment for the web application.

