package edu.hsh.favs.project.escqrs.services.productqueryservice.consumers;

import edu.hsh.favs.project.escqrs.domains.products.Product;
import edu.hsh.favs.project.escqrs.events.product.ProductAddedEvent;
import edu.hsh.favs.project.escqrs.events.product.ProductUpdatedEvent;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import edu.hsh.favs.project.escqrs.services.productqueryservice.repository.ProductQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

@EnableBinding(Sink.class)
public class ProductSaver {

  private final EntityEventProcessor eventProcessor;
  private final Logger log;
  private final ProductQueryRepository repo;

  @Autowired
  public ProductSaver(ProductQueryRepository repository) {
    this.repo = repository;
    this.log = Loggers.getLogger(ProductSaver.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(value = Sink.INPUT, condition = EntityEventProcessor.MATCHING_PRODUCTADDEDEVENT)
  public void receive(@Payload ProductAddedEvent addedEvent) {
    this.eventProcessor.handleEvent(
        addedEvent,
        event ->
            repo.addProduct(
                new Product(
                    addedEvent.getId(), addedEvent.getName().toString(), addedEvent.getCost())));
  }

  @StreamListener(value = Sink.INPUT, condition = EntityEventProcessor.MATCHING_PRODUCTUPDATEDEVENT)
  public void receive(@Payload ProductUpdatedEvent updatedEvent) {
    this.eventProcessor.handleEvent(
        updatedEvent,
        event ->
            repo.addProduct(
                new Product(
                    updatedEvent.getId(),
                    updatedEvent.getName().toString(),
                    updatedEvent.getCost())));
  }
}
