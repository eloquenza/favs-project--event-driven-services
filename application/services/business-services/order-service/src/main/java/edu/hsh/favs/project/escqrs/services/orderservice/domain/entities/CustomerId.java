package edu.hsh.favs.project.escqrs.services.orderservice.domain.entities;

import java.util.Objects;
import org.javers.core.metamodel.annotation.ValueObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ValueObject
@Table(value = "customer_ids")
public class CustomerId {

  @Id private Long id;

  public CustomerId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CustomerId)) {
      return false;
    }
    CustomerId that = (CustomerId) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String toString() {
    return "CustomerId{" + "id=" + id + '}';
  }
}
