package edu.hsh.favs.project.escqrs.events.factories;

public abstract class AbstractEventFactory<EntityT, DomainEventBaseT> {

  protected EntityT suppliedEntity;

  public abstract DomainEventBaseT createEvent(EntityT entity);

  public abstract DomainEventBaseT createEvent();

  public AbstractEventFactory<EntityT, DomainEventBaseT> supplyEntity(EntityT entity) {
    this.suppliedEntity = entity;
    return this;
  }

  public boolean isEntitySupplied() {
    return suppliedEntity != null;
  }
}
