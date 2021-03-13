package edu.hsh.favs.project.escqrs.events.product.factories;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.product.ProductAddedEvent;

public class ProductAddedEventFactory extends AbstractEventFactory<Product, ProductAddedEvent> {

  @Override
  public ProductAddedEvent createEvent(Product entity) {
    return new ProductAddedEvent(entity.getId(), entity.getName(), entity.getCost());
  }

  @Override
  public ProductAddedEvent createEvent() {
    return createEvent(suppliedEntity);
  }
}
