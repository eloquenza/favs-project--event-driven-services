package edu.hsh.favs.project.escqrs.events.order.factories;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;

public class OrderDeletedEventFactory extends AbstractEventFactory<Order, OrderDeletedEvent> {

  @Override
  public OrderDeletedEvent createEvent(Order entity) {
    return new OrderDeletedEvent(
        entity.getId(), entity.getProductId(), entity.getState().toString());
  }

  @Override
  public OrderDeletedEvent createEvent() {
    return createEvent(suppliedEntity);
  }
}
