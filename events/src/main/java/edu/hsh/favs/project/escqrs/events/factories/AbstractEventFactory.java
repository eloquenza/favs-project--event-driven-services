package edu.hsh.favs.project.escqrs.events.factories;

public interface AbstractEventFactory<EntityT, DomainEventBaseT> {
  DomainEventBaseT createEvent(EntityT entity);
}
