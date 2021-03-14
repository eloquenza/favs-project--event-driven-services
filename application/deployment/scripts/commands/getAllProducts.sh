#!/usr/bin/env bash
set -e
. "$RESTCLIENT_WORKDIR/restClient-commons.sh"

getAllProductURL="http://0.0.0.0:9000/products"
headers='Accept: application/vnd.favs-commerce.products.v1+json'

getAllProducts() {
  log "Getting all products:"
  sendGETRequest "$getAllProductURL" "$headers"
  echo ""
}
getAllProducts