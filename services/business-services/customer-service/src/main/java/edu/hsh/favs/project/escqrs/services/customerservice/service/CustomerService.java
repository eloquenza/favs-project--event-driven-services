package edu.hsh.favs.project.escqrs.services.customerservice.service;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.services.customerservice.repository.CustomerRepository;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

  private final Logger log = Logger.getLogger(CustomerService.class.getName());
  private final CustomerRepository repo;
  private final R2dbcEntityTemplate template;
  private final TransactionalOperator txOperator;

  public CustomerService(
      CustomerRepository repo, R2dbcEntityTemplate template, TransactionalOperator txOperator) {
    this.repo = repo;
    this.template = template;
    this.txOperator = txOperator;
  }

  public Mono<Customer> find(Long id) {
    return repo.findById(id);
  }

  public Mono<Customer> createCustomer(Customer customer, Consumer<Customer> callback) {
    return template
        .insert(customer)
        .doFirst(
            () ->
                log.info(
                    String.format(
                        "Database transaction is pending " + "commit for entity: %s",
                        customer.toString())))
        // Reading the entity from the DB to ensure it has been committed before trying
        // to emit the associated domain event
        .flatMap(
            committedEntity ->
                template
                    .selectOne(
                        query(where("username").is(committedEntity.getUsername())), Customer.class)
                    .single()
                    .doOnSuccess(
                        e -> log.info(String.format("Reading entity from DB: %s", e.toString()))))
        .doOnSuccess(
            entity ->
                log.info(
                    String.format(
                        "Database transaction successfully " + "completed for entity: %s",
                        entity.toString())))
        .delayUntil(entity -> Mono.fromRunnable(() -> callback.accept(entity)))
        .as(txOperator::transactional)
        .single();
  }
}
