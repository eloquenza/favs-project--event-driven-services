package edu.hsh.favs.project.escqrs.services.commons;

import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import java.util.function.Function;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;

public class DualWriteTransactionHelper {

  private DualWriteTransactionHelper() {}

  public static <EntityT, DomainEventBaseT> Mono<EntityT> deleteEntityControlFlowTemplate(
      R2dbcEntityTemplate template,
      TransactionalOperator txOperator,
      EntityT entity,
      Logger log,
      AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory,
      Source messageBroker,
      Function<EntityT, Mono<EntityT>> queryFunction) {
    return template
        // Writes the entity to DB. Errors here will result in an rollback.
        .delete(entity)
        .doFirst(() -> logPendingWriteToDB(entity, log))
        .doOnSuccess(e -> logCompletedTransactionToDB(e, log))
        .delayUntil(
            e -> beforeTransactionFinalizeTryEventEmitCallback(e, eventFactory, log, messageBroker))
        .as(txOperator::transactional)
        .single();
  }

  public static <EntityT, DomainEventBaseT> Mono<EntityT> createEntityControlFlowTemplate(
      R2dbcEntityTemplate template,
      TransactionalOperator txOperator,
      EntityT entity,
      Logger log,
      AbstractEventFactory<EntityT, DomainEventBaseT> eventFactory,
      Source messageBroker,
      Function<EntityT, Mono<EntityT>> queryFunction) {
    return template
        // Writes the entity to DB. Errors here will result in an rollback.
        .insert(entity)
        .doFirst(() -> logPendingWriteToDB(entity, log))
        // Reading the entity from the DB to ensure it has been committed before trying
        // to emit the associated domain event
        // TODO: maybe rewrite via template.getConverter.populateIdIfNecessary
        .delayUntil(committedEntity -> readEntityFromDB(committedEntity, queryFunction))
        .doOnSuccess(e -> logReadEntityFromDB(e, log))
        .doOnSuccess(e -> logCompletedTransactionToDB(e, log))
        .delayUntil(
            e -> beforeTransactionFinalizeTryEventEmitCallback(e, eventFactory, log, messageBroker))
        .as(txOperator::transactional)
        .single();
  }

  private static <EntityT> void logCompletedTransactionToDB(EntityT entity, Logger log) {
    log.info(
        String.format(
            "Database transaction successfully completed for entity: %s", entity.toString()));
  }

  // TODO: rename to logPendingCommit
  private static <EntityT> void logPendingWriteToDB(EntityT entity, Logger log) {
    log.info(
        String.format(
            "Database transaction is pending " + "commit for entity: %s", entity.toString()));
  }

  private static <EntityT> void logReadEntityFromDB(EntityT entity, Logger log) {
    log.info(String.format("Reading entity from DB: %s", entity.toString()));
  }

  private static <EntityT> Mono<EntityT> readEntityFromDB(
      EntityT entity, Function<EntityT, Mono<EntityT>> queryFunction) {
    return queryFunction.apply(entity);
  }

  private static <EntityT, DomainEventBaseT>
      Mono<EntityT> beforeTransactionFinalizeTryEventEmitCallback(
          EntityT entity,
          AbstractEventFactory<EntityT, DomainEventBaseT> factory,
          Logger log,
          Source messageBroker) {
    return Mono.fromRunnable(
        () -> {
          // If the database transaction fails, our domain event must not be sent to broker
          try {
            DomainEventBaseT event = factory.createEvent(entity);
            // Attempt to perform CQRS dual-write to message broker by sending domain event
            Message<DomainEventBaseT> message =
                MessageBuilder.withPayload(event)
                    .setHeader("eventType", event.getClass().getSimpleName())
                    .build();
            log.info(String.format("Emitting event to broker: %s", message));
            messageBroker.output().send(message, 30000L);
            // Dual-write was a success and the database transaction can commit
            log.info(
                String.format(
                    "Dual-write transaction has been successful\n"
                        + "\tEntity: %1$s\n"
                        + "\tMessage: %2$s",
                    entity, message));
          } catch (Exception ex) {
            log.error(
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
