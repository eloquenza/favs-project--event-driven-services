package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class OrderEventProcessor {

  private Histogram<Long> productBoughtHistogram;
  private Map<Long, Long> orderProductMap;
  private final Logger log = Loggers.getLogger(OrderEventProcessor.class.getName());

  public OrderEventProcessor() {
    productBoughtHistogram = new Histogram<>();
    orderProductMap = new HashMap<>();
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderCreatedEvent'")
  public void receive(@Payload OrderCreatedEvent orderCreatedEvent) {
    log.info("OrderCreatedEvent received: " + orderCreatedEvent.toString());
    Long boughtProductId = orderCreatedEvent.getProductId();
    orderProductMap.put(orderCreatedEvent.getId(), boughtProductId);
    productBoughtHistogram.addEntry(boughtProductId);
    log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderUpdatedEvent'")
  public void receive(@Payload OrderUpdatedEvent orderUpdatedEvent) {
    log.info("OrderUpdatedEvent received: " + orderUpdatedEvent.toString());
    if (orderUpdatedEvent.getProductId() != null) {
      Long changedProductId = orderUpdatedEvent.getProductId();
      Long oldProductId = orderProductMap.get(orderUpdatedEvent.getId());
      orderProductMap.replace(orderUpdatedEvent.getId(), oldProductId, changedProductId);
      productBoughtHistogram.removeEntry(oldProductId);
      productBoughtHistogram.addEntry(changedProductId);
      log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
    } else {
      this.log.info("Event consumed, no processing happened. REASON: no productIds were updated.");
    }
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderDeletedEvent'")
  public void receive(@Payload OrderDeletedEvent orderDeletedEvent) {
    log.info("OrderDeletedEvent received: " + orderDeletedEvent.toString());
    Long cancelledOrderProductId = orderDeletedEvent.getProductId();
    orderProductMap.remove(orderDeletedEvent.getId());
    productBoughtHistogram.removeEntry(cancelledOrderProductId);
    log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
  }
}
