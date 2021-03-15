package edu.hsh.favs.project.escqrs.services.productqueryservice.service;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.services.commons.exceptions.EntityNotFoundException;
import edu.hsh.favs.project.escqrs.services.productqueryservice.repository.ProductQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Service
public class ProductQueryService {

  private final Logger log = Loggers.getLogger(ProductQueryService.class.getName());
  private final ProductQueryRepository repo;

  @Autowired
  public ProductQueryService(ProductQueryRepository repo) {
    this.repo = repo;
  }

  public Mono<Product> findProductById(Long productId) {
    return repo.findById(productId)
        .switchIfEmpty(
            Mono.error(
                () -> {
                  throw new EntityNotFoundException("No product with this id can be found.");
                }));
  }

  public Flux<Product> findAllProducts() {
    return repo.getAllProducts();
  }
}
