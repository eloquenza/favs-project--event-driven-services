package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.eventprocessors;

import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis.AmountOfProductsBoughtMarketAnalysis;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.config.EventSink;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class OrderEventProcessor extends EntityEventProcessor {

  private final AmountOfProductsBoughtMarketAnalysis analysis;

  public OrderEventProcessor() {
    super(Loggers.getLogger(CustomerEventProcessor.class.getName()));
    this.analysis = new AmountOfProductsBoughtMarketAnalysis();
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderCreatedEvent'")
  public void receive(@Payload OrderCreatedEvent orderCreatedEvent) {
    handleEvent(
        orderCreatedEvent,
        event -> {
          this.analysis.addProductIdEntry(
              orderCreatedEvent.getId(), orderCreatedEvent.getProductId());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderUpdatedEvent'")
  public void receive(@Payload OrderUpdatedEvent orderUpdatedEvent) {
    handleEvent(
        orderUpdatedEvent,
        event -> {
          if (orderUpdatedEvent.getProductId() != null) {
            this.analysis.updateProductIdEntry(
                orderUpdatedEvent.getId(), orderUpdatedEvent.getProductId());
            this.logHistogram();
          } else {
            this.log.info(
                "Event consumed, no processing happened. REASON: no productIds were updated.");
          }
        });
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = "headers['eventType']=='OrderDeletedEvent'")
  public void receive(@Payload OrderDeletedEvent orderDeletedEvent) {
    handleEvent(
        orderDeletedEvent,
        event -> {
          this.analysis.removeProductIdEntry(orderDeletedEvent.getId());
          this.logHistogram();
        });
  }

  private void logHistogram() {
    this.log.info("Displaying the updated 'products bought' histogram: " + analysis);
  }
}
