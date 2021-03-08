package edu.hsh.favs.project.escqrs.events;

import java.io.Serializable;
import java.util.Objects;

public abstract class DomainBaseEvent<IdT extends Number, DomainTypeT> implements Serializable {

  private IdT id;
  private Long createdAt;
  private Long lastModified;

  public DomainBaseEvent() {}

  public IdT getId() {
    return id;
  }

  public DomainBaseEvent<IdT, DomainTypeT> setId(IdT id) {
    this.id = id;
    return this;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public DomainBaseEvent<IdT, DomainTypeT> setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public Long getLastModified() {
    return lastModified;
  }

  public DomainBaseEvent<IdT, DomainTypeT> setLastModified(Long lastModified) {
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
    if (!(o instanceof DomainBaseEvent)) {
      return false;
    }
    DomainBaseEvent<?, ?> that = (DomainBaseEvent<?, ?>) o;
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
