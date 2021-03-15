package edu.hsh.favs.project.escqrs.services.commons.eventprocessing;

import java.util.function.Consumer;
import reactor.util.Logger;

/** TODO */
public class EntityEventProcessor {

  public static final String HEADER_KEY_EVENTTYPE = "eventType";
  public static final String CONDITION_HEADERS_MATCHING =
      "headers['" + HEADER_KEY_EVENTTYPE + "']==";
  public static final String MATCHING_CUSTOMERCREATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'CustomerCreatedEvent'";
  public static final String MATCHING_CUSTOMERDELETEDEVENT =
      CONDITION_HEADERS_MATCHING + "'CustomerDeletedEvent'";
  public static final String MATCHING_CUSTOMERUPDATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'CustomerUpdatedEvent'";
  public static final String MATCHING_ORDERPLACEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderPlacedEvent'";
  public static final String MATCHING_ORDERDELETEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderDeletedEvent'";
  public static final String MATCHING_ORDERUPDATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderUpdatedEvent'";
  public static final String MATCHING_PRODUCTADDEDEVENT =
      CONDITION_HEADERS_MATCHING + "'ProductAddedEvent'";
  public static final String MATCHING_PRODUCTREMOVEDEVENT =
      CONDITION_HEADERS_MATCHING + "'ProductRemovedEvent'";
  public static final String MATCHING_PRODUCTUPDATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'ProductUpdatedEvent'";

  private final Logger log;

  public EntityEventProcessor(Logger log) {
    this.log = log;
  }

  public <EventT> void handleEvent(EventT event, Consumer<EventT> handler) {
    Class<EventT> clazz = (Class<EventT>) event.getClass();
    String eventClassName = clazz.getName();
    this.log.info(eventClassName + " received: " + event.toString());
    try {
      handler.accept(event);
    } catch (Exception e) {
      this.log.error(
          String.format(
              "Error during processing %1$s: %2$s - %3$s", eventClassName, event.toString(), e));
      throw e;
    }
  }
}
