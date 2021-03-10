package edu.hsh.favs.project.escqrs.events.order.factories;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;

public class OrderUpdatedEventFactory extends AbstractEventFactory<Order, OrderUpdatedEvent> {

  @Override
  public OrderUpdatedEvent createEvent(Order entity) {
    return insertOnlyNonNullFields(entity);
  }

  @Override
  public OrderUpdatedEvent createEvent() {
    return insertOnlyNonNullFields(suppliedEntity);
  }

  private OrderUpdatedEvent insertOnlyNonNullFields(Order entity) {
    OrderUpdatedEvent event = new OrderUpdatedEvent();
    event.setId(entity.getId());
    if (entity.getCustomerId() != null) {
      event.setCustomerId(entity.getCustomerId());
    }
    if (entity.getProductId() != null) {
      event.setProductId(entity.getProductId());
    }
    if (entity.getState() != null) {
      event.setState(entity.getState().toString());
    }
    return event;
  }
}
