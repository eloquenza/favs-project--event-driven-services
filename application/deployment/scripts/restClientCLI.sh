#!/usr/bin/env bash
set -e
export RESTCLIENT_WORKDIR=$(cd $(dirname $0) && pwd)
. "$RESTCLIENT_WORKDIR/restClient-commons.sh"


help() {
  cli_name=${0##*/}
  echo "
$cli_name
FAVS-commerce bash REST API client
Usage: $cli_name [command]
Commands:
  populateProductsDB    populate the 'product-query-service' DB with some products
  addProduct [json]     add one product
  getAllProducts        get all products
  <empty>   Help
"
  exit 1
}

case "$1" in
  populateProductsDB|insertTestEntities)
    "$RESTCLIENT_WORKDIR/commands/insertDefaultProducts.sh"
    ;;
  addProduct)
    "$RESTCLIENT_WORKDIR/commands/createProduct.sh" "$2"
    ;;
  updateProduct)
    "$RESTCLIENT_WORKDIR/commands/updateProduct.sh" "$2" "$3"
    ;;
  getAllProducts)
    "$RESTCLIENT_WORKDIR/commands/getAllProducts.sh"
    ;;
  getProduct)
    "$RESTCLIENT_WORKDIR/commands/getProduct.sh" "$2"
    ;;
  *)
    help
    ;;
esac
