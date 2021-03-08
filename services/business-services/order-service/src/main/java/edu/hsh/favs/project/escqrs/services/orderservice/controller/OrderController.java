package edu.hsh.favs.project.escqrs.services.orderservice.controller;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderCreatedEventFactory;
import edu.hsh.favs.project.escqrs.services.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/orders", produces = OrderController.MEDIATYPE_ORDER_JSON_V1)
public class OrderController {

  public static final String MEDIATYPE_ORDER_JSON_V1 =
      "application/vnd.favs-commerce.orders" + ".v1+json";
  private final Logger log = Loggers.getLogger(OrderController.class.getName());
  private final OrderService service;
  private final Source messageBroker;
  private final OrderCreatedEventFactory createEventFactory;

  @Autowired
  public OrderController(Source messageBroker, OrderService service) {
    this.messageBroker = messageBroker;
    this.service = service;
    this.createEventFactory = new OrderCreatedEventFactory();
  }

  @GetMapping(path = "{orderId}")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> getOrder(@PathVariable("orderId") Long orderId) {
    Assert.state(orderId != null, "During getOrder: orderId must not be null");
    return service.findOrderById(orderId);
  }

  @PostMapping(path = "", consumes = OrderController.MEDIATYPE_ORDER_JSON_V1)
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> createOrder(@RequestBody Mono<Order> body) {
    Assert.state(body != null, "During CreateOrder: Order payload must not be null");

    log.info("Logging createOrder request: " + body);
    return body.flatMap(order -> service.createOrder(order, createEventFactory, messageBroker));
  }

  @GetMapping(path = "")
  @ResponseStatus(code = HttpStatus.OK)
  public Flux<Order> getAllOrders() {
    return service.findAllOrders();
  }
}
