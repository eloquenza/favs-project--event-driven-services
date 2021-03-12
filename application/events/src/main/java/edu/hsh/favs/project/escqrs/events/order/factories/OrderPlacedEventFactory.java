package edu.hsh.favs.project.escqrs.events.order.factories;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.order.OrderPlacedEvent;

public class OrderPlacedEventFactory extends AbstractEventFactory<Order, OrderPlacedEvent> {

  @Override
  public OrderPlacedEvent createEvent(Order entity) {
    return new OrderPlacedEvent(
        entity.getId(),
        entity.getCustomerId(),
        entity.getProductId(),
        entity.getState().toString());
  }

  @Override
  public OrderPlacedEvent createEvent() {
    return createEvent(suppliedEntity);
  }
}
