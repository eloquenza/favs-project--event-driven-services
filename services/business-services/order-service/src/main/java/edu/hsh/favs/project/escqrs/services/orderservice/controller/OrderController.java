package edu.hsh.favs.project.escqrs.services.orderservice.controller;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.domains.orders.OrderState;
import edu.hsh.favs.project.escqrs.services.orderservice.service.OrderService;
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
import org.springframework.web.client.HttpClientErrorException;
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

  @Autowired
  public OrderController(OrderService service) {
    this.service = service;
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
    return body.flatMap(order -> service.createOrder(order));
  }

  @PutMapping(path = "{orderId}", consumes = OrderController.MEDIATYPE_ORDER_JSON_V1)
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> updateOrder(
      @PathVariable("orderId") Long orderId, @RequestBody Order updatedOrder) {
    Assert.state(updatedOrder != null, "Order payload must not equal null");
    Assert.state(orderId != null, "orderId must not equal null");

    log.info("Logging updateOrder request for order with id " + orderId + ": " + updatedOrder);
    try {
      return service.updateOrder(orderId, updatedOrder);
    } catch (UnsupportedOperationException e) {
      log.info(e.toString());
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.toString());
    }
  }

  @PutMapping(path = "{orderId}/cancel")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> cancelOrder(@PathVariable("orderId") Long orderId) {
    Assert.state(orderId != null, "orderId must not equal null");

    log.info("Logging cancelOrder request for order with id " + orderId);
    return changeOrderState(orderId, OrderState.CANCELLED);
  }

  @PutMapping(path = "{orderId}/deliver")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> deliverOrder(@PathVariable("orderId") Long orderId) {
    Assert.state(orderId != null, "orderId must not equal null");

    log.info("Logging deliverOrder request for order with id " + orderId);
    return changeOrderState(orderId, OrderState.DELIVERED);
  }

  @DeleteMapping(value = "{orderId}")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Order> deleteOrder(@PathVariable("orderId") Long orderId) {
    Assert.state(orderId != null, "orderId must not equal null");
    log.info("Logging deleteOrder request for order id: " + orderId);
    return service.deleteOrder(orderId);
  }

  @GetMapping(path = "")
  @ResponseStatus(code = HttpStatus.OK)
  public Flux<Order> getAllOrders() {
    return service.findAllOrders();
  }

  private Mono<Order> changeOrderState(Long orderId, OrderState newState) {
    try {
      return service.updateOrder(orderId, new Order().setId(orderId).setState(newState));
    } catch (UnsupportedOperationException e) {
      log.info(e.toString());
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.toString());
    }
  }
}
