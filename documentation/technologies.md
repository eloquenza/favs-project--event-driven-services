# Technologies used

In order to keep the findings of this project as technology-agnostic as possible, the use of strongly opinionated frameworks (e.g. Axon Framework) was avoided, which completely conceal typical event-driven architectural concepts and potentially other important technical details. Therefore, the project relies on libraries, frameworks and other technologies that provide sufficient support for only one feature.
This is essentially the UNIX philosophy - do one thing well.

## Language

* [Java][java]

## Frameworks

* [Spring (Spring Boot, Spring Webflux, Spring Data, Spring Cloud, Spring Cloud Stream)][spring]

## Infrastructure software, platforms, middlewares

* Databases: [PostgreSQL][postgresql]
* Message broker/event broker: [Apache Kafka][kafka]
* Virtualization: [Docker][docker]

## Most important libraries

* [Spring Boot][spring-boot] - Helps us to implement our microservices and their REST API endpoints
* [Spring Cloud Stream][spring-cloud-stream] - Helps us to interact with Kafka without having to deal with low-level Kafka-related code
* [Spring R2DBC][spring-r2dbc] - Handles our DB transactions in a reactive programming fashion, provides us with an ORM.
* [LiquiBase][liquibase] - Handles the creation of our tables on our DBs, defines the table schemas.
* [Javers][javers] - Helps us to create "diffs" of 2 object instances to quickly figure out what changed between them. Used in the update operations for all of our entities.
* [Apache Avro][avro] - Defines the data (de)serialization format and helps us to define schemas for acceptable/valid data for each Kafka topic.

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
