package edu.hsh.favs.project.escqrs.events.customer.factories;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerUpdatedEvent;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;

public class CustomerUpdatedEventFactory
    implements AbstractEventFactory<Customer, CustomerUpdatedEvent> {

  @Override
  public CustomerUpdatedEvent createEvent(Customer entity) {
    return new CustomerUpdatedEvent(
        entity.getId(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getUsername(),
        entity.getAge());
  }
}
