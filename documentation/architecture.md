# Architecture overview

This example aims to implement an e-commerce shop.
The e-commerce shop's domain is rather simple model as it just comprises of customers, orders and products, but there is a high degree of complexity involved due to the fact that each domain is tightly coupled with each other.
Each customer is able to place orders for specific products, i.e. a customer has a list of orders they placed in the past, which each order having a list of products.
If each of these domains is implemented as a single microservice, that means that in order to place an order for a specific customer, the `OrderService` would have to ask the `CustomerService` if the specified customerId exists and repeat the same with the `ProductService` and the specified productId.
Without the right architectural practice, this could degrade into a tightly coupled system where each microservice would have to directly interact with the other ones in order to send HTTP requests on REST APIs.

This could quickly degrade into a unmaintable system if we imagine a bigger scope for our system, something like Amazon.
For a large amount of customers we would need to scale up our system from one instance to many instances, maybe even set up in different data centers closer to the individual customer's country.
The Operations department would have to work tightly with the Development department to figure out a way how multiple scaled up instances of our services can interact with each other without losing data consistency, making sure how each service can reach one of the multiple available instances other services it needs to interact with.

With the correct event-driven architecture, this gets drastically more simple, as none of the microservices are interacting with each other. They simply emit events that describe what has happened.
If a new customer has created themself an account on our shop, the `CustomerService` simply publishes an `CustomerCreatedEvent` to the event broker and each microservice subscribe to these events and perform the needed actions.
For example, a `OrderService` would subscribe to these `CustomerCreatedEvent`s to locally save the ids of created customer to make sure that orders cannot be created for an non-existent customer.
Scaling up multiple instances of a specific service is a non-factor then.
Multiple `CustomerService`s can simply be started, a load-balancer on top of these will figure how which one of these instances will handle the creation of the new customer and that instance will simply publish the appropriate event to the event bus.
Now, if we have scaled up the `OrderService` to multiple instances, they all can simply subscribe to this newly published event.

While the scope of this system is rather small and the example above could seem constructed specially to fit such a use case, one can still imagine the event-driven architecture being a valid architecture solution for such a system.

## Container diagram

The following diagram displays the whole event-driven system, including which microservices it is made of and how they are interacting.
It also shows a discovery service and a gateway service.

These are used to:

* guard the microservices from direct communication from/to outside of the system, which allows operations to add an TLS termination proxy such that the microservices themselves are kept from the TLS overhead during communication;
* to implement service discovery and registration;
* to implement a form of load-balancing through the `discovery-service` as multiple microservices can register themselves and the `gateway-service` then asks the `discovery-service` for once instance of the needed service without needing to know how to reach every single instance.

This reference example has 4 microservices, each with their own database, all communicating through the centralized, shared event bus which is realized by the use of Apache Kafka.
Our Apache Kafka instance is the event bus but also our event store.
It permanently stores all the events that happened in our system through which the concept of "Event Sourcing" is realized.
We can recreate a projection of the needed domain data by consuming all appropriate Kafka topics.

![High-level container overview](./diagrams/container-overview.png)

## Container specifications

### FAVS-commerce self-implemented services

|                          Service Name                          | Spring Boot | Spring Cloud |  ORM  |   Messaging  |     Service Type     |
|:--------------------------------------------------------------:|:-----------:|:------------:|:-----:|:------------:|:--------------------:|
|              [customer-service][customer-service]              |    2.4.3    |   2020.0.1   | R2DBC | Apache Kafka |        Domain        |
|                 [order-service][order-service]                 |    2.4.3    |   2020.0.1   | R2DBC | Apache Kafka |        Domain        |
|       [product-command-service][product-command-service]       |    2.4.3    |   2020.0.1   | R2DBC | Apache Kafka |        Domain        |
|         [product-query-service][product-query-service]         |    2.4.3    |   2020.0.1   | R2DBC | Apache Kafka |        Domain        |
| [business-intelligence-service][business-intelligence-service] |    2.4.3    |   2020.0.1   |  N/A  | Apache Kafka |       Aggregate      |
|             [discovery-service][discovery-service]             |    2.4.3    |   2020.0.1   |  N/A  |      N/A     |    Netflix Eureka    |
|               [gateway-service][gateway-service]               |    2.4.3    |   2020.0.1   |  N/A  |      N/A     | Spring Cloud Gateway |

The `discovery-service` and `gateway-service` are special kind of "self-implemented" services.
While they would fit more into the following table, they are implemented by providing a self-built microservice that use Netflix Eureka or Spring Cloud Gateway as dependencies and are enabled by annotations.
This is needed because:

* for `gateway-service`, each system somehow needs to define their own routes, either via `application.yml` or a programmable way.
* for `discovery-service` and `gateway-service` you might want to define special profiles based on the environment in which you are launching them or want to add them into some kind of metrics-service like micrometer.io.

### Needed services for infrastructure purposes

|           Service Name          |                               Version                               |                  Service Type                 |
|:-------------------------------:|:-------------------------------------------------------------------:|:---------------------------------------------:|
|      [kafka][compose-file]      |                [confluentinc/cp-kafka:latest][kafka]                |    Message broker/event bus and event store   |
|    [zookeeper][compose-file]    |            [confluentinc/cp-zookeeper:latest][zookeeper]            | Hierarchical key-value store needed for Kafka |
| [schema-registry][compose-file] | [confluentinc/cp-schema-registry:latest][confluent-schema-registry] |      Central repository for data schemas      |
|   [customer-db][compose-file]   |                    [postgres:latest][postgresql]                    |                    Database                   |
|     [order-db][compose-file]    |                    [postgres:latest][postgresql]                    |                    Database                   |
|    [product-db][compose-file]   |                    [postgres:latest][postgresql]                    |                    Database                   |

## Advantages of this architecture and other used architectural concepts

TODO: Write down, that our services adhere to the reactive manifesto
TODO: Write down that we are even using Functional Reactive Programming, a new asynchronous programming paradigm

Describes a new asynchronous programming paradigm in which the availability of new information drives the logic forward rather than having control flow driven by a thread-of-execution.
For this, each problem will be decomposed into multiple discrete, disjoint steps that can be executed in an asynchronous and nonblocking way.
These steps then are composed to form a workflow, i.e. solve the afore-decomposed problem.

TODO: add CQRS description, write how they replicated data for the read model by sourcing the event streams (topics) in kafka
TODO: explain concepts used
TODO: describe how to showcase ES experiment, i.e. how newly scaled up instances of a service are reading the event stream to recreate the current state
TODO: Describe why we use database per service pattern
TODO: Write down how to access eureka, how load-balancing is implemented.
TODO: Write down somewhere, why it is important that all different event types for the same entity are written to the same kafka topic - reason: ordering is important.

How does the schema-registry work?

5.7. What Happened During Processing?

Let's try to understand what exactly happened with our example application:

    The producer built the Kafka message using the Employee object
    The producer registered the employee schema with the schema registry to get a schema version ID, this either creates a new ID or reuses the existing one for that exact schema
    Avro serialized the Employee object using the schema
    Spring Cloud put the schema-id in the message headers
    The message was published on the topic
    When the message came to the consumer, it read the schema-id from the header
    The consumer used schema-id to get the Employee schema from the registry
    The consumer found a local class that could represent that object and deserialized the message into it

https://www.baeldung.com/spring-cloud-stream-kafka-avro-confluent

* Services can use the database paradigm that fits their use case the best.

[postgresql]: https://www.postgresql.org/
[spring]: https://spring.io/
[spring-boot]: https://spring.io/projects/spring-boot
[spring-r2dbc]: https://spring.io/projects/spring-data-r2dbc
[spring-cloud]: https://spring.io/projects/spring-cloud
[spring-cloud-stream]: https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/
[zookeeper]: https://zookeeper.apache.org/
[confluent-schema-registry]: https://docs.confluent.io/platform/current/schema-registry/index.html
[kafka]: https://www.confluent.io/what-is-apache-kafka/
[compose-file]: ../application/docker-compose.yml
[customer-service]: ../application/services/business-services/customer-service
[product-command-service]: ../application/services/business-services/product-command-service
[product-query-service]: ../application/services/business-services/product-query-service
[order-service]: ../application/services/business-services/order-service
[business-intelligence-service]: ../application/services/business-services/business-intelligence-service
[discovery-service]: ../application/services/infrastructure-services/discovery-service
[gateway-service]: ../application/services/infrastructure-services/gateway-service
