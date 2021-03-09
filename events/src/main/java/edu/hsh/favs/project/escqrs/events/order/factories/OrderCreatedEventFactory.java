package edu.hsh.favs.project.escqrs.events.order.factories;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;

public class OrderCreatedEventFactory implements AbstractEventFactory<Order, OrderCreatedEvent> {

  @Override
  public OrderCreatedEvent createEvent(Order entity) {
    return new OrderCreatedEvent(
        entity.getId(),
        entity.getCustomerId(),
        entity.getProductId(),
        entity.getState().toString());
  }
}
