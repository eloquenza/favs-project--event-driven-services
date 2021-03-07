package edu.hsh.favs.project.escqrs.services.customerservice.controller;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.services.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/customers", produces = CustomerController.MEDIATYPE_CUSTOMER_JSON_V1)
public class CustomerController {

  public static final String MEDIATYPE_CUSTOMER_JSON_V1 =
      "application/vnd.favs-commerce.customers.v1+json";
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

  @PostMapping(value = "", consumes = MEDIATYPE_CUSTOMER_JSON_V1)
  @ResponseStatus(code = HttpStatus.CREATED)
  public Mono<Customer> createCustomer(@RequestBody Mono<Customer> body) {
    log.info("Logging createCustomer request: " + body);
    // Execute an dual-write of entity to local database and event to shared Kafka broker
    return body.flatMap(
        customer -> service.createCustomer(customer, this::onTransactionCommitEmitEventCallback));
  }

  private void onTransactionCommitEmitEventCallback(Customer entity) {
    // If the database transaction fails, our domain event must not be sent to broker
    try {
      CustomerCreatedEvent event = new CustomerCreatedEvent(entity);
      // Attempt to perform CQRS dual-write to message broker by sending domain event
      Message<CustomerCreatedEvent> message = MessageBuilder.withPayload(event).build();
      log.info(String.format("Emitting event to broker: %s", message));
      messageBroker.output().send(message, 30000L);
      // Dual-write was a success and the database transaction can commit
      log.info(
          String.format(
              "Dual-write transaction has been successful\n"
                  + "\tEntity: %1$s\n"
                  + "\tMessage: %2$s",
              entity, message));
    } catch (Exception ex) {
      log.error(
          String.format(
              "A dual-write transaction to the message broker has failed: %s", entity.toString()),
          ex);
      // This error will cause the database transaction to be rolled back
      throw new HttpClientErrorException(
          HttpStatus.INTERNAL_SERVER_ERROR, "A transactional error occurred");
    }
  }
}
