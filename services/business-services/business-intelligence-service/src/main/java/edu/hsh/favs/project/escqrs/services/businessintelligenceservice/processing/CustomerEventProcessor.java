package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerUpdatedEvent;
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
public class CustomerEventProcessor {

  private Histogram<Integer> customerAgeHistogram;
  private Map<Long, Integer> customerAgeMap;
  private final Logger log = Loggers.getLogger(CustomerEventProcessor.class.getName());

  public CustomerEventProcessor() {
    customerAgeHistogram = new Histogram<>();
    customerAgeMap = new HashMap<>();
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerCreatedEvent'")
  public void receive(@Payload CustomerCreatedEvent createEvent) {
    try {
      log.info("CustomerCreatedEvent received: " + createEvent.toString());
      Integer ageOfCreatedCustomer = createEvent.getAge();
      Long customerId = createEvent.getId();
      customerAgeMap.put(customerId, ageOfCreatedCustomer);
      customerAgeHistogram.addEntry(ageOfCreatedCustomer);
      log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
    } catch (Exception e) {
      log.error(
          String.format(
              "Error during processing CustomerCreatedEvent: %s", createEvent.toString(), e));
      throw e;
    }
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerDeletedEvent'")
  public void receive(@Payload CustomerDeletedEvent deleteEvent) {
    try {
      log.info("CustomerDeletedEvent received: " + deleteEvent.toString());
      Integer ageOfDeletedCustomer = deleteEvent.getAge();
      Long customerId = deleteEvent.getId();
      customerAgeMap.remove(customerId);
      customerAgeHistogram.removeEntry(ageOfDeletedCustomer);
      log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
    } catch (Exception e) {
      log.error(
          String.format(
              "Error during processing CustomerDeletedEvent: %s", deleteEvent.toString(), e));
      throw e;
    }
  }

  @StreamListener(
      value = EventSink.CUSTOMER_INPUT,
      condition = "headers['eventType']=='CustomerUpdatedEvent'")
  public void receive(@Payload CustomerUpdatedEvent updatedEvent) {
    try {
      log.info("CustomerUpdatedEvent received: " + updatedEvent.toString());
      Integer oldAge = customerAgeMap.get(updatedEvent.getId());
      Integer newAge = updatedEvent.getAge();
      customerAgeMap.replace(updatedEvent.getId(), oldAge, newAge);
      customerAgeHistogram.removeEntry(oldAge);
      customerAgeHistogram.addEntry(newAge);
      log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
      log.info("Displaying the customer age map: ");
      customerAgeMap.forEach((key, value) -> log.info(key + ":" + value));
    } catch (Exception e) {
      log.error(
          String.format(
              "Error during processing CustomerUpdatedEvent: %s", updatedEvent.toString(), e));
      throw e;
    }
  }
}
