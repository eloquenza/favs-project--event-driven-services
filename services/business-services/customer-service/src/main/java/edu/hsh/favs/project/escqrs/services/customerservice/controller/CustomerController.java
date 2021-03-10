package edu.hsh.favs.project.escqrs.services.customerservice.controller;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.services.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @Autowired
  public CustomerController(CustomerService service) {
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
    return body.flatMap(customer -> service.createCustomer(customer));
  }

  @PutMapping(value = "{customerId}", consumes = MEDIATYPE_CUSTOMER_JSON_V1)
  @ResponseStatus(code = HttpStatus.CREATED)
  public Mono<Customer> updateCustomer(
      @PathVariable("customerId") Long customerId, @RequestBody Customer updatedCustomer) {
    Assert.state(updatedCustomer != null, "Customer payload must not equal null");
    Assert.state(customerId != null, "CustomerId must not equal null");
    Assert.state(
        updatedCustomer.getId().equals(customerId),
        "CustomerId supplied in the URI path does not match the customerId in the payload");

    log.info("Logging updateCustomer request: " + updatedCustomer);
    // Execute an dual-write of entity to local database and event to shared Kafka broker
    return service.updateCustomer(customerId, updatedCustomer);
  }

  @DeleteMapping(value = "{customerId}")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Customer> deleteCustomer(@PathVariable("customerId") Long customerId) {
    Assert.state(customerId != null, "CustomerId must not equal null");
    // TODO: improve message to clarify that the customerId for the to be deleted customer is logged
    log.info("Logging deleteCustomer request: " + customerId);
    // Execute an dual-write of entity to local database and event to shared Kafka broker
    return service.deleteCustomer(customerId);
  }
}
