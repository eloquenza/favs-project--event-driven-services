package edu.hsh.favs.project.escqrs.services.productservice.controller;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.services.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/products", produces = ProductController.MEDIATYPE_PRODUCT_JSON_V1)
public class ProductController {

  public static final String MEDIATYPE_PRODUCT_JSON_V1 =
      "application/vnd.favs-commerce.products.v1+json";
  private final Logger log = Loggers.getLogger(ProductController.class.getName());
  private final ProductService service;

  @Autowired
  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping(path = "{productId}")
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Product> getProduct(@PathVariable("productId") Long productId) {
    Mono<Product> result = service.findProductById(productId);
    log.info("Logging getProduct request" + result);
    return service.findProductById(productId);
  }

  @PostMapping(path = "", consumes = ProductController.MEDIATYPE_PRODUCT_JSON_V1)
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Product> addProduct(@RequestBody Mono<Product> body) {
    Assert.state(body != null, "Product payload must not be null");

    log.info("Logging addProduct request: " + body);
    return body.flatMap(product -> service.addProduct(product));
  }

  @GetMapping(path = "")
  @ResponseStatus(code = HttpStatus.OK)
  public Flux<Product> getAllProducts() {
    log.info("Logging findAllProducts request" + service.findAllProducts());
    return service.findAllProducts();
  }

  @PutMapping(path = "{productId}", consumes = ProductController.MEDIATYPE_PRODUCT_JSON_V1)
  @ResponseStatus(code = HttpStatus.OK)
  public Mono<Product> updateProduct(
      @PathVariable("productId") Long productId, @RequestBody Product updatedProduct) {
    Assert.state(updatedProduct != null, "Product payload must not equal null");
    Assert.state(productId != null, "productId must not equal null");

    log.info(
        "Logging updateProduct request for product with id " + productId + ": " + updatedProduct);
    return service.updateProduct(productId, updatedProduct);
  }
}
