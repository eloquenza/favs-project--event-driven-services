- Feature Name: record-techology-decisions

# 1. Record informations about decisions made for development / technology to use

## Status
[status]: #status

Accepted

## Context
[context]: #context

Recording informations about the decisions taken for the development of this project helps to remember why I have done something.

## Decision
[decision]: #decision

For this project, the following technologies will be used.

### Language

* [Java][java]

### Frameworks

* [Spring (Spring Boot, Spring Webflux, Spring Data, Spring Cloud, Spring Cloud Stream)][spring]

### Infrastructure software, platforms, middlewares

* Databases: [PostgreSQL][postgresql]
* Message broker/event broker: [Apache Kafka][kafka]
* Virtualization: [Docker][docker]

### Most important libraries

* [Spring Boot][spring-boot] - Helps us to implement our microservices and their REST API endpoints
* [Spring Cloud Stream][spring-cloud-stream] - Helps us to interact with Kafka without having to deal with low-level Kafka-related code
* [Spring R2DBC][spring-r2dbc] - Handles our DB transactions in a reactive programming fashion, provides us with an ORM.
* [LiquiBase][liquibase] - Handles the creation of our tables on our DBs, defines the table schemas.
* [Javers][javers] - Helps us to create "diffs" of 2 object instances to quickly figure out what changed between them. Used in the update operations for all of our entities.
* [Apache Avro][avro] - Defines the data (de)serialization format and helps us to define schemas for acceptable/valid data for each Kafka topic.

## Consequences, rationale and alternatives
[consequences]: #consequences

### Rationale against Axon as the framework to use

While it might have been easier to implement ES and CQRS via a library like Axon, that library is very opinionated about how to use it and how it applies ES and CQRS.
This does not seem like an issue now, but it makes it harder to show the principle of both concepts by hiding implementation details needed for these.
Spring Boot, Spring Cloud Stream and Kafka as a trio might further more be a better fit simply due to the fact that not all projects are greenfield projects and not all services inside a larger system will be written with the same programming languages and technologies.
This way, we can still apply the lessons learned here to other services in the future.

## Unresolved questions
[unresolved-questions]: #unresolved-questions

None currently.

[java]: https://www.java.com/
[docker]: https://www.docker.com/
[postgresql]: https://www.postgresql.org/
[mvn]: https://maven.apache.org/
[spring]: https://spring.io/
[spring-boot]: https://spring.io/projects/spring-boot
[spring-r2dbc]: https://spring.io/projects/spring-data-r2dbc
[spring-cloud]: https://spring.io/projects/spring-cloud
[spring-cloud-stream]: https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/
[javers]: https://javers.org/
[kafka]: https://kafka.apache.org/
[avro]: https://avro.apache.org/
[liquibase]: https://www.liquibase.org/
