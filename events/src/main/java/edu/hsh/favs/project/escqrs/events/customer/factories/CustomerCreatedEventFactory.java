package edu.hsh.favs.project.escqrs.events.customer.factories;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;

public class CustomerCreatedEventFactory
    implements AbstractEventFactory<Customer, CustomerCreatedEvent> {

  @Override
  public CustomerCreatedEvent createEvent(Customer entity) {
    return new CustomerCreatedEvent(entity);
  }
}
