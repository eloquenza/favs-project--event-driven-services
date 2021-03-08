package edu.hsh.favs.project.escqrs.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import edu.hsh.favs.project.escqrs.events.customer.CustomerCreatedEvent;
import edu.hsh.favs.project.escqrs.events.order.OrderCreatedEvent;
import java.io.Serializable;
import java.util.Objects;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({@Type(CustomerCreatedEvent.class), @Type(OrderCreatedEvent.class)})
public abstract class DomainEventBase<IdT extends Number, DomainTypeT> implements Serializable {

  private IdT id;
  private Long createdAt;
  private Long lastModified;

  public DomainEventBase() {}

  public IdT getId() {
    return id;
  }

  public DomainEventBase<IdT, DomainTypeT> setId(IdT id) {
    this.id = id;
    return this;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public DomainEventBase<IdT, DomainTypeT> setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public Long getLastModified() {
    return lastModified;
  }

  public DomainEventBase<IdT, DomainTypeT> setLastModified(Long lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  public abstract DomainTypeT getData();

  public abstract void setData(DomainTypeT data);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DomainEventBase)) {
      return false;
    }
    DomainEventBase<?, ?> that = (DomainEventBase<?, ?>) o;
    return getId().equals(that.getId())
        && getCreatedAt().equals(that.getCreatedAt())
        && getLastModified().equals(that.getLastModified());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getCreatedAt(), getLastModified());
  }

  @Override
  public String toString() {
    return "DomainBaseEvent{"
        + "id="
        + id
        + ", createdAt="
        + createdAt
        + ", lastModified="
        + lastModified
        + '}';
  }
}
