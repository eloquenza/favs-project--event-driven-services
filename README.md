# FAVS-commerce

FAVS-commerce is a small event-driven/reactive microservice system showcasing several concepts of typical event-driven architectures.
It was built as part of an exam for the module "Advanced aspects of distributed systems" in WS20/21.

## High-level container overview

The whole system can be started through docker-compose.
The following diagram displays how the microservices are interacting:

![High-level container overview](./documentation/diagrams/container-overview.png)

## Usage

Currently, this system does not have a GUI in any form.
To interact with the system, you should consume its API via cURL or similar tools.  
Version negotiation is done through MIME type 
negotiation, i.e. requests have to add the appropriate "Accept"-header.
In this case, it is `Accept: application/vnd.favs-commerce.entityAsPlural.v1+json` where 
`entityAsPlural` needs to be replaced with the requested entity, e.g. `products`.  
At this moment, only the v1 API exists. Therefore, you can omit the header for `GET` requests and 
still get a valid response.
Omitting the header for `POST` requests will result in a response with the HTTP status 415 - 
`Unsupported Media Type`

As all API endpoints produce or consume JSON, a tool like `jq` will help.

### Customers

Create a customer:
`curl -s -X "POST" "http://0.0.0.0:9000/customers" -H 'Content-Type: 
application/vnd.favs-commerce.customers.v1+json; charset=utf-8' -d "{\"firstName\": 
\"testFirstname\", \"lastName\": \"testLastname\", \"age\": 19, \"username\": \"testUniqueUsername\"}"`

The username has to be unique.

List a specific customer:
`curl -H "Accept: application/vnd.favs-commerce.customers.v1+json" -s "http://0.0.0.
0:9000/customers/1"`

Replace the `1` with the id of the customer you want to know more about.

Update a specific customer: 

`curl -s -X "PUT" "http://0.0.0.0:9000/customers/1" -H 'Content-Type:
application/vnd.favs-commerce.customers.v1+json; charset=utf-8' -d "{\"firstName\":
\"newFirstName\", \"lastName\": \"newLastName\", \"age\": 21, \"username\": \"newUniqueUsername\"}"`

This operation would update all fields from the specific customer.
It is also possible to update a single field of a specific customer:

`curl -s -X "PUT" "http://0.0.0.0:9000/customers/1" -H 'Content-Type:
application/vnd.favs-commerce.customers.v1+json; charset=utf-8' -d "{"age\": 23}"`

Delete a specific customer:
`curl -s -X "DELETE" "http://0.0.0.0:9000/customers/1"`

### Orders

Due to the smaller scope of this system each order, customer and product form a 1:1:1 relationship.
This means that each product has to be in its own order.

Create an order:
`curl -X "POST" "http://0.0.0.0:9000/orders" -H 'Content-Type: application/vnd.favs-commerce.
orders.v1+json; charset=utf-8' -d "{\"customerId\": 1, \"productId\": 1, \"state\": \"PLACED\"}"`

Putting the state into the payload does not matter. During the creation of an order, the state will be set to `PLACED`.
Therefore, the above request would be the same as:

`curl -X "POST" "http://0.0.0.0:9000/orders" -H 'Content-Type: application/vnd.favs-commerce.
orders.v1+json; charset=utf-8' -d "{\"customerId\": 1, \"productId\": 1}"`

Get an specific order:
`curl -H "Accept: application/vnd.favs-commerce.orders.v1+json" -s "http://0.0.0.0:9000/orders/1"`

Update a specific order:

`curl -s -X "PUT" "http://0.0.0.0:9000/orders/1" -H 'Content-Type:
application/vnd.favs-commerce.orders.v1+json; charset=utf-8' {\"productId\": 1, \"state\": \"PLACED\"}"`

For this operation the same rules apply as to the customer update operation, i.e. updating only a specific field is possible.
Valid states for the state are `{PLACED, PAID, SHIPPED, DELIVERED, CANCELLED}`.

"Pay"/"ship"/"deliver"/"cancel" a specific order:

`curl -s -X "PUT" "http://0.0.0.0:9000/orders/1/{pay,ship,deliver,cancel}"`

Get all orders:
`curl -H "Accept: application/vnd.favs-commerce.orders.v1+json" -s "http://0.0.0.0:9000/orders"`

### Products

Get all products:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products"`

Get a specific product:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products/1"`

## Development/Deployment tools

### Kafka

This displays all created topics on the Kafka broker:

`docker run -it --network=demo_default edenhill/kafkacat:1.6.0 -b kafka:29092 -L`

This command displays all events published on a specific topic, here topic `customer` (i.e. this is a consumer):

`docker run -it --network=demo_default edenhill/kafkacat:1.6.0 -b kafka:29092 -t customer -C`

If interested in reading events from a specific offset, use the `-o #` flag with the needed offset.

### PostgreSQL

If you want to enter the PostgreSQL instance and see what is on the DB, play around, manipulate data, you can do the following:

`docker-compose exec customer-db /bin/bash`.

This will start a bash shell inside the DB container.
Afterwards, you can use the psql client to interact with the database:

`psql -U postgres`

Listing all tables can be done by entering:

`\dt+`

Other than this, all commonly known SQL commands (in PostgreSQL dialect) work inside this client.

### Application containers

#### Scaling up services

`docker-compose up -d --scale $service-name=$amount-of-required-instances`

Change `$service-name` to the service that needs to be scaled up.
Change `$amount-of-required-instances` to the required amount of instances.
If the correct service name is needed, check the `docker-compose.yml` file for the declared service-names.

For example, if the `business-intelligence-service` should be scaled up to 5 instances, the appropriate command would be:

`docker-compose up -d --scale business-intelligence-service=5`
