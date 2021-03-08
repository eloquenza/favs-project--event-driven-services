package edu.hsh.favs.project.escqrs.events.order;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.DomainEventBase;

public class OrderCreatedEvent extends DomainEventBase<Long, Order> {

  private Order order;

  public OrderCreatedEvent(Order order) {
    super();
    this.order = order;
  }

  @Override
  public Order getData() {
    return order;
  }

  @Override
  public void setData(Order data) {
    this.order = data;
  }

  @Override
  public String toString() {
    return "OrderCreatedEvent{" + "order=" + order + '}';
  }
}
