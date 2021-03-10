package edu.hsh.favs.project.escqrs.services.orderservice.repository;

import edu.hsh.favs.project.escqrs.domains.orders.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface OrderRepository extends ReactiveSortingRepository<Order, Long> {

  @Query("SELECT * FROM orders;")
  Flux<Order> getAllOrders();
}
