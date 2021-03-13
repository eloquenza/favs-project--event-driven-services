#! /usr/bin/env sh

createProductURL="http://0.0.0.0:9000/products"
createProductNeededHeaders='Content-Type: application/vnd.favs-commerce.products.v1+json; charset=utf-8'

createProduct() {
  echo "Creating a product, displaying the returned product, containing the id: "
  curl -X "POST" "$createProductURL" -H "$createProductNeededHeaders" -d "$@"
  echo ""
}

createProduct "{\"name\": \"testProduct1\", \"cost\": 1}"
createProduct "{\"name\": \"testProduct2\", \"cost\": 22}"
createProduct "{\"name\": \"testProduct3\", \"cost\": 333}"
createProduct "{\"name\": \"testProduct4\", \"cost\": 4444}"
createProduct "{\"name\": \"testProduct5\", \"cost\": 55555}"