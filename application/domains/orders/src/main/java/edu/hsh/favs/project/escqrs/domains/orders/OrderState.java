package edu.hsh.favs.project.escqrs.domains.orders;

import java.io.Serializable;

public enum OrderState implements Serializable {
  PLACED,
  PAID,
  SHIPPED,
  DELIVERED,
  CANCELLED
}
