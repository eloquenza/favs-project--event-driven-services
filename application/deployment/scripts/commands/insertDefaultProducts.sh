#! /usr/bin/env sh

insertDefaultProductCatalog() {
  "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "{\"name\": \"testProduct1\", \"cost\": 1}"
  "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "{\"name\": \"testProduct2\", \"cost\": 22}"
  "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "{\"name\": \"testProduct3\", \"cost\": 333}"
  "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "{\"name\": \"testProduct4\", \"cost\": 4444}"
  "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "{\"name\": \"testProduct5\", \"cost\": 55555}"
}

insertDefaultProductCatalog