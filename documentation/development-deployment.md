# Interesing development & deployment commands

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
