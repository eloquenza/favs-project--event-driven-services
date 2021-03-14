package edu.hsh.favs.project.escqrs.services.productcommandservice.repository;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ProductCommandRepository extends ReactiveSortingRepository<Product, Long> {

  Mono<Product> findById(Long productId);

  @Query("SELECT * FROM products")
  Flux<Product> getAllProducts();
}
