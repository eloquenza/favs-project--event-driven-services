package edu.hsh.favs.project.escqrs.services.productservice.controller;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.services.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@RestController
@RequestMapping(value = "/v1/products")
public class ProductController {

    private final Logger log = Loggers.getLogger(ProductController.class.getName());
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping(path = "{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Product> getProduct(@PathVariable("productId") Long productId) {
        Mono<Product> result = service.findProductById(productId);
        log.info("Logging getProduct request" + result);
        return service.findProductById(productId);
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public Flux<Product> getAllProducts() {
        log.info("Logging findAllProducts request" + service.findAllProducts());
        return service.findAllProducts();
    }
}
