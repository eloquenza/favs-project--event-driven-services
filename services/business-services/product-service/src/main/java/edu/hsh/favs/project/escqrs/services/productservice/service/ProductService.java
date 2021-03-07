package edu.hsh.favs.project.escqrs.services.productservice.service;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.services.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

  private final ProductRepository repo;

  public ProductService(ProductRepository repo) {
    this.repo = repo;
  }

  public Mono<Product> findProductById(Long productId) {
    return repo.findById(productId);
  }

  public Flux<Product> findAllProducts() {
    return repo.getAllProducts();
  }
}
