package edu.hsh.favs.project.escqrs.services.orderservice.controller;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import edu.hsh.favs.project.escqrs.services.orderservice.service.OrderService;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/v1/orders")
public class OrderController {

    private final Logger log = Loggers.getLogger(OrderController.class.getName());
    private final OrderService service;
    private final Source messageBroker;

    @Autowired
    public OrderController(Source messageBroker, OrderService service) {
        this.messageBroker = messageBroker;
        this.service = service;
    }

    @GetMapping(path = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Order> getOrder(@PathVariable("orderId") Long orderId) {
        Assert.state(orderId != null, "During getOrder: orderId must not be null");
        return service.findOrderById(orderId);
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Order> createOrder(@RequestBody Mono<Order> order) {
        Assert.state(order != null, "During CreateOrder: Order payload must not be null");

        log.info("Logging createOrder request: " + order);
        return order.flatMap(o -> {
            return service.createOrder(o, entity -> {
                log.info(String.format("Database request is pending transaction commit to broker: %s",
                        entity.toString()));
                try {
                    OrderCreatedEvent event = new OrderCreatedEvent(entity);
                    // Attempt to perform a reactive dual-write to message broker by sending a domain event
                    Message<OrderCreatedEvent> message = MessageBuilder.withPayload(event).build();
                    messageBroker.output().send(message, 30000L);
                    // The application dual-write was a success and the database transaction
                    // can commit
                    log.info(String.format("Database transaction completed, emitted event broker: %s", message));
                } catch (Exception ex) {
                    log.error(String.format("A dual-write transaction to the message broker has " +
                                    "failed: %s", entity.toString()), ex);
                    // This error will cause the database transaction to be rolled back
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "A transactional error occurred");
                }
            });
        });
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public Flux<Order> getAllProducts() {
        return service.findAllOrders();
    }
}
