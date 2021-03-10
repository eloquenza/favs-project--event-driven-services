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
\"testFirstname\", \"lastName\": \"testLastname\", \"age\": 21, \"username\": \"testUniqueUsername\"}"`

Delete a specific customer:
`curl -s -X "DELETE" "http://0.0.0.0:9000/customers/1"`

### Orders

Due to the smaller scope of this system each order, customer and product form a 1:1:1 relationship.
This means that each product has to be in its own order.

Create an order:
`curl -X "POST" "http://0.0.0.0:9000/orders" -H 'Content-Type: application/vnd.favs-commerce.
orders.v1+json; charset=utf-8' -d "{\"orderId\": 1, \"customerId\": 1, \"productId\": 1, \"state\": \"CREATED\"}"`

Get an specific order:
`curl -H "Accept: application/vnd.favs-commerce.orders.v1+json" -s "http://0.0.0.0:9000/orders/1"`

Get all orders:
`curl -H "Accept: application/vnd.favs-commerce.orders.v1+json" -s "http://0.0.0.0:9000/orders"`

### Products

Get all products:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products"`

Get a specific product:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products/1"`