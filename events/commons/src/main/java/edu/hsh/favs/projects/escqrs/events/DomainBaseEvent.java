package edu.hsh.favs.projects.escqrs.events;

import java.io.Serializable;
import java.util.Objects;

public abstract class DomainBaseEvent<ID, DomainType> implements Serializable {

    private ID id;
    private Long createdAt;
    private Long lastModified;

    public DomainBaseEvent() {}

    public ID getId() {
        return id;
    }

    public DomainBaseEvent<ID, DomainType> setId(ID id) {
        this.id = id;
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public DomainBaseEvent<ID, DomainType> setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public DomainBaseEvent<ID, DomainType> setLastModified(Long lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public abstract DomainType getData();

    public abstract void setData(DomainType data);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainBaseEvent)) return false;
        DomainBaseEvent<?, ?> that = (DomainBaseEvent<?, ?>) o;
        return getId().equals(that.getId()) && getCreatedAt().equals(that.getCreatedAt()) && getLastModified().equals(that.getLastModified());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt(), getLastModified());
    }

    @Override
    public String toString() {
        return "DomainBaseEvent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                '}';
    }
}
