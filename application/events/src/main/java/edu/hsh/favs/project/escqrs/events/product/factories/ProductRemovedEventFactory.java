package edu.hsh.favs.project.escqrs.events.product.factories;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.product.ProductRemovedEvent;

public class ProductRemovedEventFactory extends AbstractEventFactory<Product, ProductRemovedEvent> {

  @Override
  public ProductRemovedEvent createEvent(Product entity) {
    return new ProductRemovedEvent(entity.getId(), entity.getName(), entity.getCost());
  }

  @Override
  public ProductRemovedEvent createEvent() {
    return createEvent(suppliedEntity);
  }
}
