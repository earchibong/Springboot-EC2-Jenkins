## Deploy Spring Boot MongoDB App on AWS ECS using Jenkins CI/CD
The idea for this project came from a freelance job request on upwork:

<br>

<img width="756" alt="upwork_cicd" src="https://github.com/earchibong/springboot_project/assets/92983658/58b9d072-cdc5-41a9-8a82-131838703003">

<br>

**Access full documentation for the Jenkins / Docker Compose project option <a href="https://github.com/earchibong/springboot_project/blob/main/documentation.md">here</a>**

<br>

## Proposition:

**Docker Compose**: This is the option requested by the client. It is ideal for single-host deployments and simplifies the process of managing and orchestrating containers on a local development machine or a small-scale production environment. Here are some reasons why Docker Compose might be a suitable choice for deploying this small app:

  - a. **Simplicity:** Docker Compose offers a straightforward way to define and configure your app's services in a simple YAML file. It allows you to specify the necessary dependencies, network configurations, and environment variables, making it easy to set up and run your app.

  - b. **Single-host deployments:** If the app doesn't require high scalability or fault tolerance and can run efficiently on a single host, Docker Compose provides an uncomplicated solution. It allows you to run and manage all the containers of the app on a single machine.

 - c. **Quick setup:** Docker Compose allows you to spin up your app and its dependencies with a single command. It simplifies the process of creating an isolated environment for your app, enabling easy testing and development.

 - d. **Lightweight:** Docker Compose has fewer resource requirements compared to Kubernetes. It consumes fewer system resources and has a smaller learning curve, making it an attractive option for smaller projects.

### Considerations:

- **Limited scalability:** Docker Compose is not designed for scaling across multiple hosts. It might not be suitable if the app requires dynamic scaling based on workload or if you anticipate rapid growth in the future.

- **Lack of built-in load balancing and auto-scaling:** Docker Compose does not provide built-in features for load balancing or auto-scaling. You would need to implement these functionalities manually or rely on external tools.

<br>

### Alternative Deployment Option: Kubenetes
Kubernetes is a robust container orchestration platform that excels in managing containerized applications at scale. While it may be overkill for small apps, it offers several advantages:

- **a. Scalability and resilience:** Kubernetes provides powerful scaling and self-healing capabilities. It allows you to scale your app horizontally by adding more containers or vertically by increasing the resources assigned to each container. Kubernetes also automatically restarts failed containers and ensures high availability of your app.

- **b. Load balancing:** Kubernetes distributes network traffic across multiple containers using its built-in load balancing features. This helps ensure efficient utilization of resources and better responsiveness for your app.

- **c. Container management:** Kubernetes handles container scheduling, networking, storage, and resource allocation. It provides a comprehensive framework for managing containers, making it easier to deploy and maintain your app.

- **d. Community support and ecosystem:** Kubernetes has a large and active community, which means access to extensive documentation, tutorials, and third-party tools. It integrates well with various ecosystem components like monitoring, logging, and service discovery, allowing you to leverage a wide range of complementary services.

**Considerations:**

- **Complexity:** Kubernetes has a steeper learning curve and a more complex setup compared to Docker Compose. It involves understanding concepts like Pods, Services, Deployments, and Ingress, which might be overwhelming for small projects or developers unfamiliar with Kubernetes.

- **Resource requirements**: Kubernetes consumes more system resources than Docker Compose, which might be a consideration if you have limited resources available.

In conclusion, Docker Compose is a suitable choice for small-scale deployments (which is the case here), offering simplicity, ease of setup, and low resource requirements. On the other hand, Kubernetes shines when it comes to scalability, resilience, and managing complex containerized applications. If the small app is expected to grow rapidly or demands advanced features, Kubernetes may be a more future-proof option.


<br>


**Here's the proposed architecture for the configuration:**

<br>

```

GitHub Repository (Spring Boot App)
  |
  | (1) Trigger
  v
Jenkins CI/CD Pipeline
  |
  | (2) Build
  v
Docker Build
  |
  | (3) Push
  v
ECR (Elastic Container Registry)
  |
  | (4) Deploy
  v
EC2 Instance
  |
  | (5) Run
  v
Docker Container (Spring Boot App)
  |
  | (6) Connect
  v
MongoDB (Database)


```

<br>

