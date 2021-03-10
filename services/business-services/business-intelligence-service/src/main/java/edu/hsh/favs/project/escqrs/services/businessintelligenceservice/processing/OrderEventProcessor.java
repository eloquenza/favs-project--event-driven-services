package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
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
  private final Logger log = Loggers.getLogger(OrderEventProcessor.class.getName());

  public OrderEventProcessor() {
    productBoughtHistogram = new Histogram<>();
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderCreatedEvent'")
  public void receive(@Payload OrderCreatedEvent orderCreatedEvent) {
    log.info("OrderCreatedEvent received: " + orderCreatedEvent.toString());
    Long boughtProductId = orderCreatedEvent.getProductId();
    productBoughtHistogram.addEntry(boughtProductId);
    log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderUpdatedEvent'")
  public void receive(@Payload OrderUpdatedEvent orderUpdatedEvent) {
    log.info("OrderUpdatedEvent received: " + orderUpdatedEvent.toString());
    if (orderUpdatedEvent.getProductId() != null) {
      Long boughtProductId = orderUpdatedEvent.getProductId();
      productBoughtHistogram.addEntry(boughtProductId);
      log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
    } else {
      this.log.info("Event consumed, no processing happened. REASON: no productIds were updated.");
    }
  }
}
