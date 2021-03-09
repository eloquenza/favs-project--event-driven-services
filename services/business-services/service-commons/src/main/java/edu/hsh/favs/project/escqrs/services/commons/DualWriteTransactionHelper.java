package edu.hsh.favs.project.escqrs.services.commons;

import static org.springframework.data.r2dbc.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.function.UnaryOperator;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;

public class DualWriteTransactionHelper<EntityT> {

  private final R2dbcEntityTemplate template;
  private final TransactionalOperator txOperator;
  private final Logger log;
  private final Source messageBroker;

  public DualWriteTransactionHelper(
      R2dbcEntityTemplate template,
      TransactionalOperator txOperator,
      Source messageBroker,
      Logger log) {
    this.template = template;
    this.txOperator = txOperator;
    this.messageBroker = messageBroker;
    this.log = log;
  }

  public <DomainEventBaseT> Mono<EntityT> deleteEntity(
      EntityT entity, AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory) {
    return entityOperationControlFlowTemplate(
        this.template.delete(entity),
        entity,
        eventFactory,
        // Supplying a null will result in a no-op beforeFinalize, as we do not have to do anything
        // between deleting the entity and emitting the event
        null);
  }

  public <DomainEventBaseT> Mono<EntityT> createEntity(
      EntityT entity, AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory) {
    return entityOperationControlFlowTemplate(
        this.template.insert(entity),
        entity,
        eventFactory,
        // Before transaction finalization, read the entity from the DB to ensure it has been
        // committed before trying to emit the associated domain event
        operation ->
            operation
                .delayUntil(committedEntity -> readEntityFromDB(committedEntity))
                .doOnSuccess(e -> logReadEntityFromDB(e)));
  }

  public <DomainEventBaseT> Mono<EntityT> updateEntity(
      EntityT entity, AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory) {
    return entityOperationControlFlowTemplate(
        this.template.update(entity),
        entity,
        eventFactory,
        // Doing the same as a create operation, we want to ensure that entity is on the DB
        // Before transaction finalization, read the entity from the DB to ensure it has been
        // committed before trying to emit the associated domain event
        operation ->
            operation
                .delayUntil(committedEntity -> readEntityFromDB(committedEntity))
                .doOnSuccess(e -> logReadEntityFromDB(e)));
  }

  private <DomainEventBaseT> Mono<EntityT> entityOperationControlFlowTemplate(
      Mono<EntityT> entityOperation,
      EntityT entity,
      AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory,
      UnaryOperator<Mono<EntityT>> beforeFinalize) {
    // Creates/updates/deletes the entity on/from DB. Errors here will result in an rollback.
    return entityOperation
        .doFirst(() -> logPendingTransactionCommitToDB(entity))
        .transform(mono -> beforeFinalize != null ? beforeFinalize.apply(mono) : mono)
        .doOnSuccess(e -> logCompletedTransactionToDB(e))
        .delayUntil(e -> beforeTransactionFinalizeTryEventEmitCallback(e, eventFactory))
        .as(this.txOperator::transactional)
        .single();
  }

  private void logCompletedTransactionToDB(EntityT entity) {
    this.log.info(
        String.format(
            "Database transaction successfully completed for entity: %s", entity.toString()));
  }

  private void logPendingTransactionCommitToDB(EntityT entity) {
    this.log.info(
        String.format(
            "Database transaction is pending " + "commit for entity: %s", entity.toString()));
  }

  private void logReadEntityFromDB(EntityT entity) {
    this.log.info(String.format("Reading entity from DB: %s", entity.toString()));
  }

  private Mono<EntityT> readEntityFromDB(EntityT entity) {
    try {
      return (Mono<EntityT>)
          template
              .select(entity.getClass())
              .matching(query(where("id").is(entity.getClass().getMethod("getId").invoke(entity))))
              .first();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  private <DomainEventBaseT> Mono<EntityT> beforeTransactionFinalizeTryEventEmitCallback(
      EntityT entity, AbstractEventFactory<EntityT, DomainEventBaseT> factory) {
    return Mono.fromRunnable(
        () -> {
          // If the database transaction fails, our domain event must not be sent to broker
          try {
            DomainEventBaseT event = factory.createEvent(entity);
            // Attempt to perform CQRS-needed dual-write to message broker by sending domain event
            Message<DomainEventBaseT> message =
                MessageBuilder.withPayload(event)
                    .setHeader("eventType", event.getClass().getSimpleName())
                    .build();
            this.log.info(String.format("Emitting event to broker: %s", message));
            this.messageBroker.output().send(message, 30000L);
            // Dual-write was a success and the database transaction can commit
            this.log.info(
                String.format(
                    "Dual-write transaction has been successful\n"
                        + "\tEntity: %1$s\n"
                        + "\tMessage: %2$s",
                    entity, message));
          } catch (Exception ex) {
            this.log.error(
                String.format(
                    "A dual-write transaction to the message broker has failed: %s",
                    entity.toString()),
                ex);
            // This error will cause the database transaction to be rolled back
            throw new HttpClientErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR, "A transactional error occurred");
          }
        });
  }
}
