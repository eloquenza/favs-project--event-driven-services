package edu.hsh.favs.project.escqrs.events.factories;

import edu.hsh.favs.project.escqrs.events.DomainEventBase;

public interface AbstractEventFactory<
    EntityT, DomainEventBaseT extends DomainEventBase<? extends Number, EntityT>> {
  DomainEventBaseT createEvent(EntityT entity);
}
