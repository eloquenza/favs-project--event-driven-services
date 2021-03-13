package edu.hsh.favs.project.escqrs.services.orderservice.consumers;

import edu.hsh.favs.project.escqrs.events.product.ProductAddedEvent;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import edu.hsh.favs.project.escqrs.services.orderservice.config.EventSink;
import edu.hsh.favs.project.escqrs.services.orderservice.domain.entities.ProductId;
import edu.hsh.favs.project.escqrs.services.orderservice.repository.OrderRepository;
import edu.hsh.favs.project.escqrs.services.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

// Needed to save/delete the ids of added/removed products, so only valid productIds can be
// passed for the creation of orders.
@EnableBinding(EventSink.class)
public class ProductIdSaver {

  private final EntityEventProcessor eventProcessor;
  private final Logger log;
  private final OrderRepository repository;
  private final OrderService service;
  private final R2dbcEntityTemplate template;

  @Autowired
  public ProductIdSaver(
      R2dbcEntityTemplate template, OrderRepository repository, OrderService service) {
    this.template = template;
    this.repository = repository;
    this.service = service;
    this.log = Loggers.getLogger(ProductIdSaver.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(
      value = EventSink.PRODUCT_INPUT,
      condition = EntityEventProcessor.MATCHING_PRODUCTADDEDEVENT)
  public void receive(@Payload ProductAddedEvent addedEvent) {
    this.eventProcessor.handleEvent(
        addedEvent,
        event -> template.insert(new ProductId(addedEvent.getId())).single().then().subscribe());
  }
}
