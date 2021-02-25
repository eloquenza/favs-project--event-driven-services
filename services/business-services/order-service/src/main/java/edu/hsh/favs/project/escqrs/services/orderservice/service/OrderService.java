package edu.hsh.favs.project.escqrs.services.orderservice.service;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import edu.hsh.favs.project.escqrs.services.orderservice.orders.repository.OrderRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.logging.Logger;

@Service
public class OrderService {

    private final Logger log = Logger.getLogger(OrderService.class.getName());
    private final OrderRepository repo;
    private final R2dbcEntityTemplate template;
    private final TransactionalOperator txOperator;

    public OrderService(
            OrderRepository repo,
            R2dbcEntityTemplate template,
            TransactionalOperator txOperator) {
        this.repo = repo;
        this.template = template;
        this.txOperator = txOperator;
    }

    public Mono<Order> findOrderById(Long orderId) {
        return repo.findById(orderId);
    }

    public Flux<Order> findAllOrders() {
        return repo.getAllOrders();
    }

    public Mono<Order> createOrder(
            Order order,
            Consumer<Order> callback
    ) {
        return template.insert(Order.class)
                .using(order)
                .as(txOperator::transactional)
                .doOnSuccess(callback);
    }
}
