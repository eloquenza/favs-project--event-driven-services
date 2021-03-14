#!/usr/bin/env bash
set -e
. "$RESTCLIENT_WORKDIR/restClient-commons.sh"

createProductURL="http://0.0.0.0:9000/products"
headers='Content-Type: application/vnd.favs-commerce.products.v1+json; charset=utf-8'

createProduct() {
  log "Creating a product, displaying the returned product, containing the id: "
  sendPOSTRequest "$createProductURL" "$headers" "$@"
  echo ""
}

createProduct "$1"