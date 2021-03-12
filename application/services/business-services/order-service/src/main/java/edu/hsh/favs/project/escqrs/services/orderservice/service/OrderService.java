package edu.hsh.favs.project.escqrs.services.orderservice.service;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderCreatedEventFactory;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderDeletedEventFactory;
import edu.hsh.favs.project.escqrs.events.order.factories.OrderUpdatedEventFactory;
import edu.hsh.favs.project.escqrs.services.commons.transactions.DualWriteTransactionHelper;
import edu.hsh.favs.project.escqrs.services.commons.transactions.EntityUpdater;
import edu.hsh.favs.project.escqrs.services.orderservice.repository.OrderRepository;
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

  private final Logger log = Loggers.getLogger(OrderService.class.getName());
  private final OrderRepository repo;
  private final OrderCreatedEventFactory createEventFactory;
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
    this.createEventFactory = new OrderCreatedEventFactory();
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

  public Mono<Order> createOrder(Order order) {
    return dualWriteHelper.createEntity(order, createEventFactory);
  }

  public Mono<Order> updateOrder(Long orderId, Order updatedOrder) {
    // Semantical nonsensical to transfer an order to another customer, therefore we guard ourselves
    // from clients that try to do so.
    if (updatedOrder.getCustomerId() != null) {
      throw new UnsupportedOperationException(
          "Updating the customerId of an specific order is now allowed");
    }
    return this.repo
        .findById(orderId)
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
}
