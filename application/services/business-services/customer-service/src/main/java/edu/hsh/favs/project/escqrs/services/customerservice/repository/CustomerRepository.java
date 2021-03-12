package edu.hsh.favs.project.escqrs.services.customerservice.repository;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

  Mono<Customer> findByUsername(String username);
}
