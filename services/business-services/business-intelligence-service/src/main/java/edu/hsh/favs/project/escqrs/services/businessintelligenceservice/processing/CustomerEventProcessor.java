package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;

import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Configuration
@EnableBinding(EventSink.class)
public class CustomerEventProcessor {

    private Map<Integer, Long> customerAgeHistogram = new TreeMap<>();
    private final Logger log = Loggers.getLogger(CustomerEventProcessor.class.getName());

    @StreamListener(value = EventSink.CUSTOMER_INPUT)
    public void receive(CustomerCreatedEvent customerCreatedEvent) {
        log.info("Event received: " + customerCreatedEvent.toString());
        Integer ageOfCreatedCustomer = customerCreatedEvent.getData().getAge();
        customerAgeHistogram.merge(ageOfCreatedCustomer, 1L,
                (oldValue, newValue) -> oldValue + 1L);
        log.info("New customer age histogram: " + printHistogram(customerAgeHistogram));
    }

    private String printHistogram(Map<?, ?> histogram) {
        return histogram.keySet().stream()
                .map(key -> key + "=" + histogram.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
