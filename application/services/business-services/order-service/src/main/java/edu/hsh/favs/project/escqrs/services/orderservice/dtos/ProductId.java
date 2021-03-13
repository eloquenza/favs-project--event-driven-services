package edu.hsh.favs.project.escqrs.services.orderservice.dtos;

import java.util.Objects;
import org.javers.core.metamodel.annotation.ValueObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ValueObject
@Table(value = "orderservice_products")
public class ProductId {

  @Id private Long id;

  public ProductId(Long id) {
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
    if (!(o instanceof ProductId)) {
      return false;
    }
    ProductId that = (ProductId) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String toString() {
    return "ProductId{" + "id=" + id + '}';
  }
}
