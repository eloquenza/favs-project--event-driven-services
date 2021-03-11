package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.eventprocessors;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis.CustomerWithSpecificAgeMarketAnalysis;
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
public class CustomerEventProcessor extends EntityEventProcessor {

  private final CustomerWithSpecificAgeMarketAnalysis analysis;
  private final Logger log;

  public CustomerEventProcessor() {
    super(Loggers.getLogger(CustomerEventProcessor.class.getName()));
    this.log = Loggers.getLogger(CustomerEventProcessor.class.getName());
    this.analysis = new CustomerWithSpecificAgeMarketAnalysis();
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerCreatedEvent'")
  public void receive(@Payload CustomerCreatedEvent createEvent) {
    handleEvent(
        createEvent,
        event -> {
          this.analysis.addAgeEntry(createEvent.getId(), createEvent.getAge());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerDeletedEvent'")
  public void receive(@Payload CustomerDeletedEvent deleteEvent) {
    handleEvent(
        deleteEvent,
        event -> {
          this.analysis.removeAgeEntry(deleteEvent.getId());
          this.logHistogram();
        });
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerUpdatedEvent'")
  public void receive(@Payload CustomerUpdatedEvent updatedEvent) {
    handleEvent(
        updatedEvent,
        event -> {
          this.analysis.updateAgeEntry(updatedEvent.getId(), updatedEvent.getAge());
          this.logHistogram();
        });
  }

  private void logHistogram() {
    this.log.info("Displaying the updated 'customer age' histogram: " + analysis);
  }
}
