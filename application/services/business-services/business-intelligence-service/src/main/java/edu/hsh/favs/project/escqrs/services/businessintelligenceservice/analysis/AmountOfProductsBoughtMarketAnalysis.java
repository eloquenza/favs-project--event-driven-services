package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis;

import edu.hsh.favs.project.escqrs.domains.orders.OrderState;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderPlacedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.config.EventSink;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes.EntityAnalyticWarehouse;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class AmountOfProductsBoughtMarketAnalysis {

  // Save orders and products by a id <-> id mapping,
  // implements a histogram with productId buckets to count
  // which products have been bought how many times.
  private final EntityAnalyticWarehouse<Long, Long> warehouse;
  private final EntityEventProcessor eventProcessor;
  private final Logger log;

  public AmountOfProductsBoughtMarketAnalysis() {
    this.warehouse = new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
    this.log = Loggers.getLogger(AmountOfProductsBoughtMarketAnalysis.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = EntityEventProcessor.MATCHING_ORDERPLACEDEVENT)
  public void receive(@Payload OrderPlacedEvent orderPlacedEvent) {
    this.eventProcessor.handleEvent(
        orderPlacedEvent,
        event -> {
          this.warehouse.addValueEntry(orderPlacedEvent.getId(), orderPlacedEvent.getProductId());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = EntityEventProcessor.MATCHING_ORDERUPDATEDEVENT)
  public void receive(@Payload OrderUpdatedEvent orderUpdatedEvent) {
    this.eventProcessor.handleEvent(
        orderUpdatedEvent,
        event -> {
          if (orderUpdatedEvent.getProductId() != null) {
            this.warehouse.updateValueEntry(
                orderUpdatedEvent.getId(), orderUpdatedEvent.getProductId());
            this.logHistogram();
          } else {
            this.log.info("Skip productId processing - REASON: no productIds were updated.");
          }
          if (orderUpdatedEvent
              .getState()
              .toString()
              .contentEquals(OrderState.CANCELLED.toString())) {
            this.warehouse.removeValueEntry(orderUpdatedEvent.getId());
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
      condition = EntityEventProcessor.MATCHING_ORDERDELETEDEVENT)
  public void receive(@Payload OrderDeletedEvent orderDeletedEvent) {
    this.eventProcessor.handleEvent(
        orderDeletedEvent,
        event -> {
          if (!orderDeletedEvent
              .getState()
              .toString()
              .contentEquals(OrderState.DELIVERED.toString())) {
            this.log.info("Order was never delivered, i.e. these products were never bought.");
            this.warehouse.removeValueEntry(orderDeletedEvent.getId());
            this.logHistogram();
          }
        });
  }

  private void logHistogram() {
    this.log.info("Displaying the updated 'products bought' histogram: " + warehouse);
  }
}
