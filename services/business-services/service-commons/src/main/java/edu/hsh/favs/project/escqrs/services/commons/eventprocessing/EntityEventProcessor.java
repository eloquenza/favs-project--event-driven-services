package edu.hsh.favs.project.escqrs.services.commons.eventprocessing;

import java.util.function.Consumer;
import reactor.util.Logger;

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
  public static final String MATCHING_ORDERCREATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderCreatedEvent'";
  public static final String MATCHING_ORDERDELETEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderDeletedEvent'";
  public static final String MATCHING_ORDERUPDATEDEVENT =
      CONDITION_HEADERS_MATCHING + "'OrderUpdatedEvent'";

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

  public static String createConditionMatchingEventTypeString(String eventName) {
    return "headers['" + HEADER_KEY_EVENTTYPE + "']=='" + eventName + "'";
  }
}
