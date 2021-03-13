package edu.hsh.favs.project.escqrs.services.orderservice.consumers;

import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.customer.CustomerDeletedEvent;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import edu.hsh.favs.project.escqrs.services.orderservice.dtos.CustomerId;
import edu.hsh.favs.project.escqrs.services.orderservice.repository.OrderRepository;
import edu.hsh.favs.project.escqrs.services.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.util.Logger;
import reactor.util.Loggers;

// Needed to save/delete the ids of created/deleted customers, so only valid customerIds can be
// passed
// for the creation of orders.
@EnableBinding(Sink.class)
public class CustomerIdSaver {

  private final EntityEventProcessor eventProcessor;
  private final Logger log;
  private final OrderRepository repository;
  private final OrderService service;
  private final R2dbcEntityTemplate template;

  @Autowired
  public CustomerIdSaver(
      R2dbcEntityTemplate template, OrderRepository repository, OrderService service) {
    this.template = template;
    this.repository = repository;
    this.service = service;
    this.log = Loggers.getLogger(CustomerIdSaver.class.getName());
    this.eventProcessor = new EntityEventProcessor(this.log);
  }

  @StreamListener(
      value = Sink.INPUT,
      condition = EntityEventProcessor.MATCHING_CUSTOMERCREATEDEVENT)
  public void receive(@Payload CustomerCreatedEvent createEvent) {
    this.eventProcessor.handleEvent(
        createEvent,
        event -> template.insert(new CustomerId(createEvent.getId())).single().then().subscribe());
  }

  @StreamListener(
      value = Sink.INPUT,
      condition = EntityEventProcessor.MATCHING_CUSTOMERDELETEDEVENT)
  public void receive(@Payload CustomerDeletedEvent deletedEvent) {
    this.eventProcessor.handleEvent(
        deletedEvent,
        event -> {
          repository
              .findByCustomerId(deletedEvent.getId())
              .flatMap(order -> service.deleteOrder(order.getId()))
              .doOnComplete(
                  () ->
                      template
                          .delete(new CustomerId(deletedEvent.getId()))
                          .single()
                          .then()
                          .subscribe())
              .then()
              .subscribe();
        });
  }
}
