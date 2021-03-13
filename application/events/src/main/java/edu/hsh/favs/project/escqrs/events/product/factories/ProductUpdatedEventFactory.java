package edu.hsh.favs.project.escqrs.events.product.factories;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.events.product.ProductUpdatedEvent;

public class ProductUpdatedEventFactory extends AbstractEventFactory<Product, ProductUpdatedEvent> {

  @Override
  public ProductUpdatedEvent createEvent(Product entity) {
    return insertOnlyNonNullFields(entity);
  }

  @Override
  public ProductUpdatedEvent createEvent() {
    return insertOnlyNonNullFields(suppliedEntity);
  }

  private ProductUpdatedEvent insertOnlyNonNullFields(Product entity) {
    ProductUpdatedEvent event = new ProductUpdatedEvent();
    event.setId(entity.getId());
    if (entity.getName() != null) {
      event.setName(entity.getName());
    }
    if (entity.getCost() != null) {
      event.setCost(entity.getCost());
    }
    return event;
  }
}
