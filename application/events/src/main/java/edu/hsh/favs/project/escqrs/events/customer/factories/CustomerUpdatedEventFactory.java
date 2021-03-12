package edu.hsh.favs.project.escqrs.events.customer.factories;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerUpdatedEvent;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;

public class CustomerUpdatedEventFactory
    extends AbstractEventFactory<Customer, CustomerUpdatedEvent> {

  @Override
  public CustomerUpdatedEvent createEvent(Customer entity) {
    return insertOnlyNonNullFields(entity);
  }

  @Override
  public CustomerUpdatedEvent createEvent() {
    return insertOnlyNonNullFields(suppliedEntity);
  }

  private CustomerUpdatedEvent insertOnlyNonNullFields(Customer entity) {
    CustomerUpdatedEvent event = new CustomerUpdatedEvent();
    event.setId(entity.getId());
    if (entity.getFirstName() != null) {
      event.setFirstName(entity.getFirstName());
    }
    if (entity.getLastName() != null) {
      event.setLastName(entity.getLastName());
    }
    if (entity.getUsername() != null) {
      event.setUsername(entity.getUsername());
    }
    if (entity.getAge() != null) {
      event.setAge(entity.getAge());
    }
    return event;
  }
}
