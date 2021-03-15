# How to start this system

## Locally

To start this system, open up a shell, switch into the [./application](../application) directory and execute `docker-compose up -d`.
This should start up all needed microservices and all other infrastructure services needed to allow the system to work correctly.
All containers are currently pushed to the Docker Hub.
This command should pull the needed images from there.

If you want to perform changes to the code base and want to create new container images with the newly updated applications, look into how to compile our system: [Development and Deployment](./development-deployment.md)

You can follow the start up process via `docker-compose logs -f` which simply displays all logs of each service.

## Docker Compose

Via Docker Compose it is possible to deploy locally or to a Kubernetes/Docker Swarm.
To start locally, just follow the introduction chapter of this document.

## Usage

Look into the [Usage guide](./how-to-start.md).
