#!/usr/bin/env bash
log() {
  script_name=${0##*/}
  timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
  echo "== $script_name $timestamp $1"
}

sendGETRequest() {
    if command -v jq >/dev/null 2>&1; then
        curl -s "$1" -H "$2" | jq
    else
        curl -s "$1" -H "$2"
    fi
}

sendPOSTRequest() {
    if command -v jq >/dev/null 2>&1; then
        curl -X "POST" -s "$1" -H "$2" -d "$3" | jq
    else
        curl -X "POST" -s "$1" -H "$2" -d "$3"
    fi
}

sendDELETERequest() {
    if command -v jq >/dev/null 2>&1; then
        curl -X "DELETE" -s "$1" -H "$2" | jq
    else
        curl -X "DELETE" -s "$1" -H "$2"
    fi
}

sendPUTRequest() {
    if command -v jq >/dev/null 2>&1; then
        curl -X "PUT" -s "$1" -H "$2" -d "$3" | jq
    else
        curl -X "PUT" -s "$1" -H "$2" -d "$3"
    fi
}