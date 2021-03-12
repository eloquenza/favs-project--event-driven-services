package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerUpdatedEvent;
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
public class CustomerWithSpecificAgeMarketAnalysis {

  // Save customers by id and age, has a histogram with age buckets to count
  // how many customers we have in our service with a specific age.
  private final EntityAnalyticWarehouse<Long, Integer> warehouse;
  private final EntityEventProcessor eventProcessor;
  private final Logger log;

  public CustomerWithSpecificAgeMarketAnalysis() {
    this.warehouse = new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
    this.log = Loggers.getLogger(CustomerWithSpecificAgeMarketAnalysis.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = EntityEventProcessor.MATCHING_CUSTOMERCREATEDEVENT)
  public void receive(@Payload CustomerCreatedEvent createEvent) {
    this.eventProcessor.handleEvent(
        createEvent,
        event -> {
          this.warehouse.addValueEntry(createEvent.getId(), createEvent.getAge());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = EntityEventProcessor.MATCHING_CUSTOMERDELETEDEVENT)
  public void receive(@Payload CustomerDeletedEvent deleteEvent) {
    this.eventProcessor.handleEvent(
        deleteEvent,
        event -> {
          this.warehouse.removeValueEntry(deleteEvent.getId());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = EntityEventProcessor.MATCHING_CUSTOMERUPDATEDEVENT)
  public void receive(@Payload CustomerUpdatedEvent updatedEvent) {
    this.eventProcessor.handleEvent(
        updatedEvent,
        event -> {
          this.warehouse.updateValueEntry(updatedEvent.getId(), updatedEvent.getAge());
          this.logHistogram();
        });
  }

  private void logHistogram() {
    this.log.info("Displaying the updated 'customer age' histogram: " + warehouse);
  }
}
