package edu.hsh.favs.project.escqrs.events.customer.factories;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;

public class CustomerDeletedEventFactory
    implements AbstractEventFactory<Customer, CustomerDeletedEvent> {

  @Override
  public CustomerDeletedEvent createEvent(Customer entity) {
    return new CustomerDeletedEvent(entity.getId(), entity.getUsername(), entity.getAge());
  }
}
