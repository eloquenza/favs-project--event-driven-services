package edu.hsh.favs.project.escqrs.domains.orders;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "orders")
public class Order implements Serializable {
  @Column(value = "customer_id")
  private Long customerId;

  @Id
  @Column(value = "order_id")
  private Long orderId;

  @Column(value = "product_id")
  private Long productId;

  @Column(value = "state")
  private OrderState state;

  public Order() {}

  public Order(Long customerId, Long orderId, Long productId, OrderState state) {
    this.customerId = customerId;
    this.orderId = orderId;
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

  public Long getOrderid() {
    return orderId;
  }

  public Order setOrderid(Long orderId) {
    this.orderId = orderId;
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
        && getOrderid().equals(order.getOrderid())
        && getProductId().equals(order.getProductId())
        && getState() == order.getState();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCustomerId(), getOrderid(), getProductId(), getState());
  }

  @Override
  public String toString() {
    return "Order{"
        + "customerId="
        + customerId
        + ", orderId="
        + orderId
        + ", orderedProducts="
        + productId
        + ", state="
        + state
        + '}';
  }
}
