package edu.hsh.favs.project.escqrs.services.customerservice.service;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.factories.CustomerCreatedEventFactory;
import edu.hsh.favs.project.escqrs.events.customer.factories.CustomerDeletedEventFactory;
import edu.hsh.favs.project.escqrs.events.customer.factories.CustomerUpdatedEventFactory;
import edu.hsh.favs.project.escqrs.services.commons.transactions.DualWriteTransactionHelper;
import edu.hsh.favs.project.escqrs.services.commons.transactions.EntityUpdater;
import edu.hsh.favs.project.escqrs.services.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final CustomerCreatedEventFactory createEventFactory;
  private final CustomerDeletedEventFactory deleteEventFactory;
  private final CustomerUpdatedEventFactory updateEventFactory;
  private final DualWriteTransactionHelper<Customer> dualWriteHelper;
  private final EntityUpdater<Customer> entityUpdater;

  @Autowired
  public CustomerService(
      CustomerRepository repo,
      R2dbcEntityTemplate template,
      TransactionalOperator txOperator,
      Source messageBroker) {
    this.repo = repo;
    this.createEventFactory = new CustomerCreatedEventFactory();
    this.deleteEventFactory = new CustomerDeletedEventFactory();
    this.updateEventFactory = new CustomerUpdatedEventFactory();
    this.dualWriteHelper =
        new DualWriteTransactionHelper<>(template, txOperator, messageBroker, log);
    this.entityUpdater = new EntityUpdater<>(log);
  }

  public Mono<Customer> find(Long id) {
    return repo.findById(id);
  }

  public Mono<Customer> createCustomer(Customer customer) {
    return dualWriteHelper.createEntity(customer, createEventFactory);
  }

  public Mono<Customer> deleteCustomer(Long customerId) {
    return this.repo
        .findById(customerId)
        .flatMap(customer -> dualWriteHelper.deleteEntity(customer, deleteEventFactory));
  }

  public Mono<Customer> updateCustomer(Long customerId, Customer updatedCustomer) {
    // TODO: handle error where user will supply an ID in the json body that does not match the
    // customerId given on the URI path
    // TODO: handle error where user will try to "create" an user via an update
    // ^- both errors are conceptually the same, I think
    return this.repo
        .findById(customerId)
        .flatMap(
            customer ->
                dualWriteHelper.updateEntity(
                    entityUpdater.update(customer, updatedCustomer),
                    updateEventFactory.supplyEntity(updatedCustomer.setId(customer.getId()))));
  }
}
