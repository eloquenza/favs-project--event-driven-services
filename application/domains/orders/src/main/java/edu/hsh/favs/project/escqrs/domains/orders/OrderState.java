package edu.hsh.favs.project.escqrs.domains.orders;

import java.io.Serializable;

public enum OrderState implements Serializable {
  CREATED,
  CANCELLED,
  DELIVERED
}
