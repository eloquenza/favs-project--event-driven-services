#!/usr/bin/env bash
set -e
. "$RESTCLIENT_WORKDIR/restClient-commons.sh"

getProductURL="http://0.0.0.0:9000/products"
headers='Accept: application/vnd.favs-commerce.products.v1+json'

getProduct() {
  log "Getting product with id: $1"
  sendGETRequest "getProductURL/$1" "$headers"
  echo ""
}
getProduct "$1"