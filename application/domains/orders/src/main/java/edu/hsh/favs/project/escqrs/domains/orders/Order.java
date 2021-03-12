package edu.hsh.favs.project.escqrs.domains.orders;

import java.io.Serializable;
import java.util.Objects;
import org.javers.core.metamodel.annotation.ValueObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@ValueObject
@Table(value = "orders")
public class Order implements Serializable {
  @Id private Long id;

  @Column(value = "customer_id")
  private Long customerId;

  @Column(value = "product_id")
  private Long productId;

  @Column(value = "state")
  private OrderState state;

  public Order() {}

  public Order(Long customerId, Long id, Long productId, OrderState state) {
    this.customerId = customerId;
    this.id = id;
    this.productId = productId;
    this.state = state;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Order setCustomerId(Long customerId) {
    this.customerId = customerId;
    return this;
  }

  public Long getId() {
    return id;
  }

  public Order setId(Long id) {
    this.id = id;
    return this;
  }

  public OrderState getState() {
    return state;
  }

  public Order setState(OrderState state) {
    this.state = state;
    return this;
  }

  public Long getProductId() {
    return productId;
  }

  public Order setProductId(Long productId) {
    this.productId = productId;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Order)) {
      return false;
    }
    Order order = (Order) o;
    return getCustomerId().equals(order.getCustomerId())
        && getId().equals(order.getId())
        && getProductId().equals(order.getProductId())
        && getState() == order.getState();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCustomerId(), getId(), getProductId(), getState());
  }

  @Override
  public String toString() {
    return "Order{"
        + "customerId="
        + customerId
        + ", id="
        + id
        + ", orderedProducts="
        + productId
        + ", state="
        + state
        + '}';
  }
}
