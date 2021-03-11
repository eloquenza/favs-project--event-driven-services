package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.eventprocessors;

import edu.hsh.favs.project.escqrs.domains.orders.OrderState;
import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis.AmountOfProductsBoughtMarketAnalysis;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.config.EventSink;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class OrderEventProcessor extends EntityEventProcessor {

  private final AmountOfProductsBoughtMarketAnalysis analysis;
  private final Logger log;

  public OrderEventProcessor() {
    super(Loggers.getLogger(OrderEventProcessor.class.getName()));
    this.log = Loggers.getLogger(OrderEventProcessor.class.getName());
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
            this.log.info("Skip productId processing - REASON: no productIds were updated.");
          }
          if (orderUpdatedEvent
              .getState()
              .toString()
              .contentEquals(OrderState.CANCELLED.toString())) {
            this.analysis.removeProductIdEntry(orderUpdatedEvent.getId());
            this.logHistogram();
          } else {
            this.log.info(
                "Skip updateRemoval processing - REASON: state was not changed into "
                    + OrderState.CANCELLED.toString());
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
