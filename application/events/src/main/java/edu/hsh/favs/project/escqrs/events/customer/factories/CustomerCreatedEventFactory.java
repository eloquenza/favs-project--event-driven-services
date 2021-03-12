package edu.hsh.favs.project.escqrs.events.customer.factories;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;

public class CustomerCreatedEventFactory
    extends AbstractEventFactory<Customer, CustomerCreatedEvent> {

  @Override
  public CustomerCreatedEvent createEvent(Customer entity) {
    return new CustomerCreatedEvent(
        entity.getId(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getUsername(),
        entity.getAge());
  }

  @Override
  public CustomerCreatedEvent createEvent() {
    return createEvent(suppliedEntity);
  }
}
