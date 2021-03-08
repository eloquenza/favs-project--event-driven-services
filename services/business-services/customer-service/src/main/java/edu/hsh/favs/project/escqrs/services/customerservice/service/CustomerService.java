package edu.hsh.favs.project.escqrs.services.customerservice.service;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.factories.CustomerCreatedEventFactory;
import edu.hsh.favs.project.escqrs.services.commons.DualWriteTransactionHelper;
import edu.hsh.favs.project.escqrs.services.customerservice.repository.CustomerRepository;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Service
public class CustomerService {

  private final Logger log = Loggers.getLogger(CustomerService.class.getName());
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

  public Mono<Customer> createCustomer(
      Customer customer, CustomerCreatedEventFactory eventFactory, Source messageBroker) {
    return DualWriteTransactionHelper.createEntityControlFlowTemplate(
        template,
        txOperator,
        customer,
        log,
        eventFactory,
        messageBroker,
        cust -> repo.findByUsername(cust.getUsername()));
  }
}
