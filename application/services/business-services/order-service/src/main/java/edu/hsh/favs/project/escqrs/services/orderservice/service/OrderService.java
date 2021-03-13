package edu.hsh.favs.project.escqrs.services.orderservice.service;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.domains.orders.OrderState;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderDeletedEventFactory;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderPlacedEventFactory;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderUpdatedEventFactory;
import edu.hsh.favs.project.escqrs.services.commons.exceptions.EntityNotFoundException;
import edu.hsh.favs.project.escqrs.services.commons.exceptions.IllegalEntityOperationException;
import edu.hsh.favs.project.escqrs.services.commons.transactions.DualWriteTransactionHelper;
import edu.hsh.favs.project.escqrs.services.commons.transactions.EntityUpdater;
import edu.hsh.favs.project.escqrs.services.orderservice.repository.OrderRepository;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Service
public class OrderService {

  // TODO: explicit exception if that customerId/productId does not exist
  // TODO: disallow update to invalid productId
  // TODO: Make sure bad requests will somehow be logged

  private final Logger log = Loggers.getLogger(OrderService.class.getName());
  private final OrderRepository repo;
  private final OrderPlacedEventFactory createEventFactory;
  private final OrderUpdatedEventFactory updateEventFactory;
  private final OrderDeletedEventFactory deleteEventFactory;
  private final DualWriteTransactionHelper<Order> dualWriteHelper;
  private final EntityUpdater<Order> entityUpdater;

  @Autowired
  public OrderService(
      OrderRepository repo,
      R2dbcEntityTemplate template,
      TransactionalOperator txOperator,
      Source messageBroker) {
    this.repo = repo;
    this.createEventFactory = new OrderPlacedEventFactory();
    this.updateEventFactory = new OrderUpdatedEventFactory();
    this.deleteEventFactory = new OrderDeletedEventFactory();
    this.dualWriteHelper =
        new DualWriteTransactionHelper<>(template, txOperator, messageBroker, log);
    this.entityUpdater = new EntityUpdater<>(log);
  }

  public Mono<Order> findOrderById(Long orderId) {
    return repo.findById(orderId);
  }

  public Flux<Order> findAllOrders() {
    return repo.getAllOrders();
  }

  public Mono<Order> placeOrder(Order order) {
    return dualWriteHelper.createEntity(order, createEventFactory);
  }

  public Mono<Order> updateOrder(Long orderId, Order updatedOrder) {
    // Semantical nonsensical to transfer an order to another customer, therefore we guard ourselves
    // from clients that try to do so.
    if (updatedOrder.getCustomerId() != null) {
      return Mono.error(
          () -> {
            throw new IllegalEntityOperationException(
                "Updating the customerId of an specific order is now allowed");
          });
    }
    return this.repo
        .findById(orderId)
        .switchIfEmpty(
            Mono.error(
                () -> {
                  throw new EntityNotFoundException("No order with this id can be found.");
                }))
        .filter(order -> isValidStateTransition(order.getState(), updatedOrder.getState()))
        .switchIfEmpty(
            Mono.error(
                () -> {
                  throw new IllegalEntityOperationException(
                      "Invalid state transition, to transition into "
                          + updatedOrder.getState()
                          + " the order needs to be in one of the following states: "
                          + Arrays.toString(updatedOrder.getState().previousState()));
                }))
        .flatMap(
            order ->
                dualWriteHelper.updateEntity(
                    entityUpdater.update(order, updatedOrder),
                    updateEventFactory.supplyEntity(updatedOrder.setId(order.getId()))));
  }

  public Mono<Order> deleteOrder(Long orderId) {
    return this.repo
        .findById(orderId)
        .flatMap(order -> dualWriteHelper.deleteEntity(order, deleteEventFactory));
  }

  private boolean isValidStateTransition(OrderState oldState, OrderState newState) {
    return oldState.hasNextState() && Arrays.asList(oldState.nextState()).contains(newState);
  }
}
