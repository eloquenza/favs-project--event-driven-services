package edu.hsh.favs.project.escqrs.services.commons.transactions;

import static org.springframework.data.r2dbc.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import edu.hsh.favs.project.escqrs.events.factories.AbstractEventFactory;
import edu.hsh.favs.project.escqrs.services.commons.eventprocessing.EntityEventProcessor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.UnaryOperator;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;

/**
 * This class provides our services with a unified mechanism to perform
 * our CRUD transactions.
 * This is a delicate process because we need to publish an event after each
 * successfully committed transaction, but we need to rollback the transaction
 * if for some reason the event could not be transmitted.
 * This makes sense if we view published events as true facts that represent new
 * information.
 * If this event would not represent a correct fact, i.e. a new customer is
 * perceived as created despite not being available in the CustomerService
 * a compensation event would have to be transmitted, that instantly tells other
 * services that this customer does not exist in the system.
 * While that is also a possible solution, it was decided that for this system
 * it is just simpler to only publish events after their operation is completed.
 * 
 * Therefore, to ensure each service handles these local DB transaction and the
 * publishing of said events in the same way, this class was created.
 * Through this class, each service is provided with:
 * * a entity create operation that publishes the appropriate CreateEvent,
 * * a entity delete operation that publishes the appropriate DeleteEvent,
 * * a entity update operation that publishes the appropriate UpdateEvent.
 *
 * This class however does not try to figure out which the correct event type
 * is for the currently performed operation.
 * Instead, it relies on the service to provide it with the appropriate event
 * factory which is then used to create the event, if and only if the DB
 * transaction has successfully committed.
 * Furthermore, it ensures each of these transactions and published events
 * are correctly logged.
 */
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
              // Using reflection to access getId here as the entities do not have a shared
              // superclass
              // but all are having an getId method.
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
            DomainEventBaseT event =
                factory.isEntitySupplied() ? factory.createEvent() : factory.createEvent(entity);
            // Attempt to perform CQRS-needed dual-write to message broker by sending domain event
            Message<DomainEventBaseT> message =
                MessageBuilder.withPayload(event)
                    // supply an eventType header so consumers can do content-based routing, i.e.
                    // figure out which event is to be sent to which consuming function
                    .setHeader(
                        EntityEventProcessor.HEADER_KEY_EVENTTYPE, event.getClass().getSimpleName())
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
            throw new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A transactional error occurred: " + ex.getMessage());
          }
        });
  }
}
