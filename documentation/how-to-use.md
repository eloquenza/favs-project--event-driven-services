# How to interact with the FAVS-Commerce system

TODO: DIVIDE API ENDPOINT USAGE FROM ACTUAL WORKFLOW DESCRIPTION?

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

As all API endpoints produce or consume JSON, a tool like `jq` will help with making the JSON more readable in the CLI.

Furthermore, a few constraints have been introduced for the first version of this system to simplify the development process:

* Orders and products are in a 1:1 relationship,
* Customers have unlimited budget,
* Products are not really shipped & delivered to the customers (which is fine, because they currently do not lose their money anyways),
* Products are infinitely available

## Customers

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

## Orders

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

## Products

Add a product:

`curl -X "POST" "http://0.0.0.0:9000/products" -H 'Content-Type: application/vnd.favs-commerce.products.v1+json; charset=utf-8' -d "{\"name\": \"testProduct1\", \"cost\": 111}"`

Update a specific product:

`curl -X "PUT" "http://0.0.0.0:9000/products/1" -H 'Content-Type: application/vnd.favs-commerce.products.v1+json; charset=utf-8' -d "{\"name\": \"updateProductName1\", \"cost\": 222}" | jq`

For this operation the same rules apply as to the customer/order update operation, i.e. updating only a specific field is possible.

Deleting a product:

This is not possible due to the fact that the system would like to be able to provide an audit log for all possible orders.
To do so, it would be counterproductive to delete products and therefore the operation is not supported.
Ideally but not yet implemented, this should be able to soft-delete products so they are not viewable in an GUI but still present in the database to see which orders have been placed for which products.

Get all products:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products"`

Get a specific product:
`curl -H "Accept: application/vnd.favs-commerce.products.v1+json" -s "http://0.0.0.0:9000/products/1"`
