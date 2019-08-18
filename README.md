# Simple Portal (Spring Boot / JWT / Docker / GCP-GKE / AWS-ECS)

This is a sample project containing 3 containers working together to deliver a basic app.

 * Auth
 * Team
 * Web-portal

## Containers

### Auth

### Team

### Web-portal

## Deploying

### On AWS / ECS

There are a number of things to configure to get this working as needed in your environment.

* Ensure valid AWS connection using AWS CLI
* You'll need a valid Route53 public domain - that is world accessible.
    * In my case I am using `thanatopho.be`

#### Build the VPC, ECS Cluster and LoadBalancer

This is all of the common networking required to deploy and test out the ECS containers.

```
cd ecs
make create-cluster
```

#### Build & Publish the Containers

```
cd ecs
make create-ecr
$(aws ecr get-login --no-include-email --region ap-southeast-2)
make push
```

#### Launch Postgres container

```
cd ecs
make deploy-postgres
```

#### Launch auth container

```
cd ecs
make deploy-auth
```

#### Launch team container

```
cd ecs
make deploy-team
```