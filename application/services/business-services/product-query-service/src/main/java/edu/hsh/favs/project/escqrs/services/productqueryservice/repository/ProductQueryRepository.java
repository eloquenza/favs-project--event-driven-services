package edu.hsh.favs.project.escqrs.services.productqueryservice.repository;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductQueryRepository {

  private final Map<Long, Product> products;

  public ProductQueryRepository() {
    this.products = new HashMap<>();
  }

  public Mono<Product> findById(Long productId) {
    return Mono.just(products.get(productId));
  }

  public Flux<Product> getAllProducts() {
    return Flux.fromIterable(products.values());
  }

  public Product addProduct(Product product) {
    return products.put(product.getId(), product);
  }
}
