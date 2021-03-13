# Interesing development & deployment commands

## Development

### Compiling

### Creating the docker container images

Executing the Maven lifecycle phase `install` will compile the associated application and re-create its docker container image.
Doing this for all services is possible by executing the following command while having the top-level `application` folder as the working directory:

`mvn install`

It is advised to always recompile and rebuild all containers in order to avoid subtle bugs.
However, if needed, doing so for a specific service can be done by supplying its project name to the Maven command:

`mvn -pl maven-subproject-name install`

For example, to rebuild the customer-service, the following would work:

`mvn -pl customer-service install`

### Special cases

#### Subtle bugs occuring while only rebuilding/restarting a specific container

Sometimes, for reasons currently unknown, the newly rebuilt docker image will not be started if you kept all other services running.
If this is the case, simply stop all current containers, remove all docker containers and start all services up again:

```bash
docker-compose down
docker system prune
mvn install
docker-compose up -d
```

#### Rebuilding the register-schemas container

If the dependencies for `register-schemas`, i.e. the `events` Maven sub-project change, you manually need to re-create that container.
The easiest way to do so would be:

`docker-compose build --no-cache register-schemas`

## Docker

### Start all services

`docker-compose up -d`

### Start only a specific service

`docker-compose start service_name`

For, e.g. customer-service:
`docker-compose start customer-service`

### Stop only a specific service

`docker-compose stop service_name`

For, e.g. customer-service:
`docker-compose stop customer-service`

### Restart only a specific service

`docker-compose restart service_name`

For, e.g. customer-service:
`docker-compose restart customer-service`

### Execute a specific command inside a service container

`docker-compose exec service_name command`

Open a `bash` shell inside e.g. customer-service:
`docker-compose exec customer-service /bin/bash`

### Shutdown all services

`docker-compose down`

### See the logs for all services

`docker-compose logs -f`

### See logs for a specific service

`docker-compose logs -f service_name`

For, e.g. customer-service:
`docker-compose logs -f customer-service`

## Kafka

This displays all created topics on the Kafka broker:

`docker run -it --network=demo_default edenhill/kafkacat:1.6.0 -b kafka:29092 -L`

This command displays all events published on a specific topic, here topic `customer` (i.e. this is a consumer):

`docker run -it --network=demo_default edenhill/kafkacat:1.6.0 -b kafka:29092 -t customer -C`

If interested in reading events from a specific offset, use the `-o #` flag with the needed offset.

## PostgreSQL

If you want to enter the PostgreSQL instance and see what is on the DB, play around, manipulate data, you can do the following:

`docker-compose exec database-container-name /bin/bash`.

This will start a bash shell inside the specified DB container.

Possible `database-container-name`s are:

* `customer-db`
* `product-db`
* `order-db`

Afterwards, you can use the psql client to interact with the database:

`psql -U postgres`

Listing all tables can be done by entering:

`\dt+`

Other than this, all commonly known SQL commands (in PostgreSQL dialect) work inside this client.

## Application containers

### Scaling up services

`docker-compose up -d --scale $service-name=$amount-of-required-instances`

Change `$service-name` to the service that needs to be scaled up.
Change `$amount-of-required-instances` to the required amount of instances.
If the correct service name is needed, check the `docker-compose.yml` file for the declared service-names.

For example, if the `business-intelligence-service` should be scaled up to 5 instances, the appropriate command would be:

`docker-compose up -d --scale business-intelligence-service=5`
