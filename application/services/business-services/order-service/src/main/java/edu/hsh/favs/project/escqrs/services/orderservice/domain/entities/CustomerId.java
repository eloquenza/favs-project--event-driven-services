package edu.hsh.favs.project.escqrs.services.orderservice.domain.entities;

import java.util.Objects;
import org.javers.core.metamodel.annotation.ValueObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Simple POJO to allow the entity mapper to generate a valid mapping between the 'customer_ids'
 * table, which only contains customerIds, and a class that essentially is only a Long field. In a
 * better language, this should have been only a typedef declaration.
 */
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
