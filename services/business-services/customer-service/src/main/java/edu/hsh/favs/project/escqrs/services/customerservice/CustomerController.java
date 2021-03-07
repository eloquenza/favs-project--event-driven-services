package edu.hsh.favs.project.escqrs.services.customerservice;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(
        value = "/customers",
        produces = CustomerController.MEDIATYPE_CUSTOMER_JSON_V1
)
public class CustomerController {

    public static final String MEDIATYPE_CUSTOMER_JSON_V1 = "application/vnd.favs-commerce.customers.v1+json";
    private final Logger log = Loggers.getLogger(CustomerController.class.getName());
    private final CustomerService service;
    private final Source messageBroker;

    @Autowired
    public CustomerController(Source messageBroker, CustomerService service) {
        this.messageBroker = messageBroker;
        this.service = service;
    }

    @GetMapping(path = "{customerId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Customer> getCustomer(@PathVariable("customerId") Long customerId) {
        return service.find(customerId);
    }

    @PostMapping(
            value = "",
            consumes = MEDIATYPE_CUSTOMER_JSON_V1
    )
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Customer> createCustomer(@RequestBody Mono<Customer> customer) {
        log.info("Logging createCustomer request: " + customer);
        return customer.flatMap(cust -> {
                return service.createCustomer(cust, entity -> {
                    // If the database operation fails, a domain event should not be sent to the message broker
                    log.info(String.format("Database request is pending transaction commit to broker: %s",
                            entity.toString()));
                    try {
                        CustomerCreatedEvent event = new CustomerCreatedEvent(entity);
                        // Attempt to perform a reactive dual-write to message broker by sending a domain event
                        Message<CustomerCreatedEvent> message = MessageBuilder.withPayload(event).build();
                        messageBroker.output().send(message, 30000L);
                        // The application dual-write was a success and the database transaction
                        // can commit
                        log.info(String.format("Database transaction completed, emitted event " +
                                "broker: %s", message));
                    } catch (Exception ex) {
                        log.error(String.format("A dual-write transaction to the " +
                                        "message broker" +
                                        " has failed: %s",
                                entity.toString()), ex);
                        // This error will cause the database transaction to be rolled back
                        throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "A transactional error occurred");
                    }
                });
            }
        );
    }
}