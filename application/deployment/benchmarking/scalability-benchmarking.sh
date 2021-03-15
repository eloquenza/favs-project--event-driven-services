#!/usr/bin/env bash

# This creates a latency benchmark.
# We are first warming up the caches of all involved services by performing
# a few demo requests.
# Afterwards, we perform the real latency test over 10 mins.
# This is done via 4 threads with 4 active HTTP connections, with 1000
# requests / sec, combined over all 4 threads.
# We would like to use more threads/connections to mimic the higher
# customer load, but this test was sadly performed on the developer's
# machine.

# We are requesting a specific product because we want to measure how
# multiple service impact the latency for clients, and want to avoid the
# unneeded overhead that might occur due to serialization into JSON if
# we would request all products at the same time.

latencyTest() {
    # Warming up JVM, CPU and RAM caches
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    wrk2 -t4 -c4 -d30s -R1000 --latency http://0.0.0.0:9000/products/1
    # Producing the real latency test over 10 mins
    wrk2 -t4 -c4 -d600s -R1000 --latency http://0.0.0.0:9000/products/1
}
