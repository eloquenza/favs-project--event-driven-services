#!/usr/bin/env bash
set -e
. "$RESTCLIENT_WORKDIR/restClient-commons.sh"

updateProductURL="http://0.0.0.0:9000/products"
headers='Content-Type: application/vnd.favs-commerce.products.v1+json; charset=utf-8'

updateProduct() {
  log "Updating the product with id: $1"
  sendPUTRequest "$updateProductURL/$1" "$headers" "$2"
  echo ""
}

updateProduct "$1" "$2"