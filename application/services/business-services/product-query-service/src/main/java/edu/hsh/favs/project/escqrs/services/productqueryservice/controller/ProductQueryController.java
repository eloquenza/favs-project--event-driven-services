package edu.hsh.favs.project.escqrs.services.productqueryservice.controller;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.services.productqueryservice.service.ProductQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/products", produces = ProductQueryController.MEDIATYPE_PRODUCT_JSON_V1)
public class ProductQueryController {

  public static final String MEDIATYPE_PRODUCT_JSON_V1 =
      "application/vnd.favs-commerce.products.v1+json";
  private final Logger log = Loggers.getLogger(ProductQueryController.class.getName());
  private final ProductQueryService service;

  @Autowired
  public ProductQueryController(ProductQueryService service) {
    this.service = service;
  }

  @GetMapping(path = "{productId}")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Product> getProduct(@PathVariable("productId") Long productId) {
    log.info("Logging getProduct request for product with id: " + productId);
    return service.findProductById(productId);
  }

  @GetMapping(path = "")
  @ResponseStatus(code = HttpStatus.OK)
  public Flux<Product> getAllProducts() {
    log.info("Logging findAllProducts request");
    return service.findAllProducts();
  }
}
