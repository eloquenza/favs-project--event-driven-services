# FAVS-commerce

FAVS-commerce is a small event-driven/reactive microservice system showcasing several concepts of typical event-driven architectures.
It was built as part of an exam for the module "Advanced aspects of distributed systems" in WS20/21.

## High-level container overview

The whole system can be started through docker-compose.
The following diagram displays how the microservices are interacting:

## Usage

Currently, this system does not have a GUI in any form.
To interact with the system, you should consume it's API via cURL or similar programs.
Furthermore the URIs are work-in-progress and should be reworked.
The naming scheme is a bit cumbersome.

### Customers

Create a customer:
`curl -s -X "POST" "http://0.0.0.0:9000/customer/v1/customers/create" -H 'Content-Type: application/json; charset=utf-8' -d "{\"firstName\": \"testFirstname\", \"lastName\": \"testLastname\", \"age\": 19, \"username\": \"testUniqueUsername\"}"`

The username has to be unique.

List a specific customer:
`curl -s "http://0.0.0.0:9000/customer/v1/customers/{id}`

Replace the `{id}` with the id of the customer you want to know more about.

### Orders

Create an order:
`curl -s -X "POST" "http://0.0.0.0:9000/orders/v1/orders/create" -H 'Content-Type: application/json; charset=utf-8' -d "{\"orderId\": 1, \"customerId\": 1, \"productId\": 4, \"state\": \"CREATED\"}"`

Due to the smaller scope of this system each order, customer and product form a 1:1:1 relationship.
This means that each product has to be in its own order.

### Products

List all products:
`curl -s "http://0.0.0.0:9000/products/v1/products/all"`
