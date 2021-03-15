package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis;

import edu.hsh.favs.project.escqrs.domains.orders.OrderState;
import edu.hsh.favs.project.escqrs.events.order.OrderDeletedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderPlacedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.config.EventSink;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes.EntityAnalyticWarehouse;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class ProductMarketAnalysis {

  // Save orders and products by a id <-> id mapping,
  // implements a histogram with productId buckets to count
  // which products have been bought how many times.
  private final EntityAnalyticWarehouse<Long, Long> productsCustomerWereInterestedInWarehouse;
  private final EntityAnalyticWarehouse<Long, Long> boughtProductsWarehouse;
  private final EntityEventProcessor eventProcessor;
  private final Supplier<Stream<String>> finalOrderStates =
      () ->
          Stream.of(
              OrderState.PAID.toString(),
              OrderState.SHIPPED.toString(),
              OrderState.DELIVERED.toString());
  private final Logger log;

  public ProductMarketAnalysis() {
    this.productsCustomerWereInterestedInWarehouse =
        new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
    this.boughtProductsWarehouse =
        new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
    this.log = Loggers.getLogger(ProductMarketAnalysis.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(
      value = EventSink.ORDER_INPUT,
      condition = EntityEventProcessor.MATCHING_ORDERPLACEDEVENT)
  public void receive(@Payload OrderPlacedEvent orderPlacedEvent) {
    this.eventProcessor.handleEvent(
        orderPlacedEvent,
        event -> {
          this.productsCustomerWereInterestedInWarehouse.addValueEntry(
              orderPlacedEvent.getId(), orderPlacedEvent.getProductId());
          this.logHistograms();
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
            this.productsCustomerWereInterestedInWarehouse.updateValueEntry(
                orderUpdatedEvent.getId(), orderUpdatedEvent.getProductId());
            this.logHistograms();
          } else {
            this.log.info("Skip productId processing - REASON: no productIds were updated.");
          }
          if (orderUpdatedEvent
              .getState()
              .toString()
              .contentEquals(OrderState.CANCELLED.toString())) {
            this.productsCustomerWereInterestedInWarehouse.removeValueEntry(
                orderUpdatedEvent.getId());
            this.logHistograms();
          } else {
            this.log.info(
                "Skip updateRemoval processing - REASON: state was not changed into "
                    + OrderState.CANCELLED.toString());
          }
          if (orderUpdatedEvent.getState().toString().contentEquals(OrderState.PAID.toString())) {
            Long productId =
                this.productsCustomerWereInterestedInWarehouse.getMappedValue(
                    orderUpdatedEvent.getId());
            this.boughtProductsWarehouse.addValueEntry(orderUpdatedEvent.getId(), productId);
            this.logHistograms();
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
          if (finalOrderStates
              .get()
              .noneMatch(orderDeletedEvent.getState().toString()::contentEquals)) {
            this.log.info("Order was never finalized, i.e. these products were never bought.");
            this.productsCustomerWereInterestedInWarehouse.removeValueEntry(
                orderDeletedEvent.getId());
            this.logHistograms();
          }
        });
  }

  private void logHistograms() {
    this.logInterestingProductsHistogram();
    this.logBoughtProductsHistogram();
  }

  private void logInterestingProductsHistogram() {
    this.log.info(
        "Displaying the updated 'products customers were interested in' histogram: "
            + productsCustomerWereInterestedInWarehouse);
  }

  private void logBoughtProductsHistogram() {
    this.log.info("Displaying the updated 'products bought' histogram: " + boughtProductsWarehouse);
  }
}
