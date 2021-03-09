package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configuration
@EnableBinding(EventSink.class)
public class CustomerEventProcessor {

  private Histogram<Integer> customerAgeHistogram;
  private final Logger log = Loggers.getLogger(CustomerEventProcessor.class.getName());

  public CustomerEventProcessor() {
    customerAgeHistogram = new Histogram<>();
  }

  @StreamListener(value = EventSink.CUSTOMER_INPUT)
  public void receive(CustomerCreatedEvent createEvent) {
    try {
      log.info("CustomerCreatedEvent received: " + createEvent.toString());
      Integer ageOfCreatedCustomer = createEvent.getAge();
      customerAgeHistogram.addEntry(ageOfCreatedCustomer);
      log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
    } catch (Exception e) {
      log.error(
          String.format(
              "Error during processing CustomerCreatedEvent: %s", createEvent.toString(), e));
      throw e;
    }
  }

  @StreamListener(value = EventSink.CUSTOMER_INPUT)
  public void receive(CustomerDeletedEvent deleteEvent) {
    try {
      log.info("CustomerDeletedEvent received: " + deleteEvent.toString());
      Integer ageOfCreatedCustomer = deleteEvent.getAge();
      customerAgeHistogram.removeEntry(ageOfCreatedCustomer);
      log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
    } catch (Exception e) {
      log.error(
          String.format(
              "Error during processing CustomerCreatedEvent: %s", deleteEvent.toString(), e));
      throw e;
    }
  }
}
