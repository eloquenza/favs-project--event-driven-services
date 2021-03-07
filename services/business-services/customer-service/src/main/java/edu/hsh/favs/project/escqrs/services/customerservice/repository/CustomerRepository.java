package edu.hsh.favs.project.escqrs.services.customerservice.repository;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveSortingRepository<Customer, Long> {

  Flux<Customer> findByUsername(String username);

  Flux<Customer> findByFirstName(String firstName);

  Flux<Customer> findByLastName(String lastName);

  Mono<Customer> findById(Long id);

  Flux<Customer> findByAge(int age);
}
