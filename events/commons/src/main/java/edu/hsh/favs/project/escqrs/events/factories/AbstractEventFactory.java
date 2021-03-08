package edu.hsh.favs.project.escqrs.events.factories;

import edu.hsh.favs.project.escqrs.events.DomainBaseEvent;

public interface AbstractEventFactory<
    EntityT, DomainEventBaseT extends DomainBaseEvent<? extends Number, EntityT>> {
  DomainEventBaseT createEvent(EntityT entity);
}
