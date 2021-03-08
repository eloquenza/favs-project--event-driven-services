package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
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

  @StreamListener(value = EventSink.ORDER_INPUT)
  public void listen(@Payload OrderCreatedEvent orderCreatedEvent) {
    log.info("OrderCreatedEvent received: " + orderCreatedEvent.toString());
    Long boughtProductId = orderCreatedEvent.getData().getProductId();
    productBoughtHistogram.addEntry(boughtProductId);
    log.info("Displaying the updated 'products bought' histogram: " + productBoughtHistogram);
  }
}
