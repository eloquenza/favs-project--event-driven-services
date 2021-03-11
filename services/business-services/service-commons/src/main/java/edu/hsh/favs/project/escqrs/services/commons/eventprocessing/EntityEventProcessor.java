package edu.hsh.favs.project.escqrs.services.commons.eventprocessing;

import java.util.function.Consumer;
import reactor.util.Logger;

public class EntityEventProcessor {

  public static final String HEADER_KEY_EVENTTYPE = "eventType";
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
