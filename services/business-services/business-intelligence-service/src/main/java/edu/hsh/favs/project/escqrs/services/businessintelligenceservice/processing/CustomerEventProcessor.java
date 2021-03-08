package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
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
  public void receive(CustomerCreatedEvent customerCreatedEvent) {
    log.info("CustomerCreatedEvent received: " + customerCreatedEvent.toString());
    Integer ageOfCreatedCustomer = customerCreatedEvent.getData().getAge();
    customerAgeHistogram.addEntry(ageOfCreatedCustomer);
    log.info("Displaying the updated 'customer age' histogram: " + customerAgeHistogram);
  }
}
