package edu.hsh.favs.project.escqrs.services.customerservice.controller;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.events.customer.factories.CustomerCreatedEventFactory;
import edu.hsh.favs.project.escqrs.services.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
  private final CustomerCreatedEventFactory createEventFactory;

  @Autowired
  public CustomerController(Source messageBroker, CustomerService service) {
    this.messageBroker = messageBroker;
    this.service = service;
    this.createEventFactory = new CustomerCreatedEventFactory();
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
        customer -> service.createCustomer(customer, createEventFactory, messageBroker));
  }
}
