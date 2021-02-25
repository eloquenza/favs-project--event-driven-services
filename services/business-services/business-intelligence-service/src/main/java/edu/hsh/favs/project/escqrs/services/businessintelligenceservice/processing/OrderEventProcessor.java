package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Configuration
@EnableBinding(EventSink.class)
public class OrderEventProcessor {

    private Map<Long, Long> productBoughtHistogram = new TreeMap<>();
    private final Logger log = Loggers.getLogger(OrderEventProcessor.class.getName());

    @StreamListener(value = EventSink.ORDER_INPUT)
    public void listen(@Payload OrderCreatedEvent orderCreatedEvent) {
        log.info("Event received: " + orderCreatedEvent.toString());
        Long boughtProductId = orderCreatedEvent.getData().getProductId();
        productBoughtHistogram.computeIfPresent(boughtProductId,
                (k, v) -> v++
        );
        log.info("New customer age histogram: " + printHistogram(productBoughtHistogram));
    }

    private String printHistogram(Map<?, ?> histogram) {
        return histogram.keySet().stream()
                .map(key -> key + "=" + histogram.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
