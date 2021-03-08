package edu.hsh.favs.project.escqrs.events.customer;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.DomainBaseEvent;

public class CustomerCreatedEvent extends DomainBaseEvent<Long, Customer> {

  private Customer cust;

  public CustomerCreatedEvent() {
    super();
  }

  public CustomerCreatedEvent(Customer cust) {
    this.cust = cust;
  }

  @Override
  public Customer getData() {
    return cust;
  }

  @Override
  public void setData(Customer data) {
    this.cust = data;
  }

  @Override
  public String toString() {
    return "CustomerCreatedEvent{" + "cust=" + cust + '}';
  }
}
