package edu.hsh.favs.project.escqrs.domains.orders;

import java.io.Serializable;

public enum OrderState implements Serializable {
  PLACED {
    @Override
    public boolean hasPreviousState() {
      return false;
    }

    @Override
    public OrderState[] previousState() {
      return new OrderState[] {};
    }

    @Override
    public OrderState[] nextState() {
      return new OrderState[] {PAID, CANCELLED};
    }

    @Override
    public boolean hasNextState() {
      return true;
    }
  },
  PAID {
    @Override
    public boolean hasPreviousState() {
      return true;
    }

    @Override
    public OrderState[] previousState() {
      return new OrderState[] {PLACED};
    }

    @Override
    public OrderState[] nextState() {
      return new OrderState[] {SHIPPED, CANCELLED};
    }

    @Override
    public boolean hasNextState() {
      return true;
    }
  },
  SHIPPED {
    @Override
    public boolean hasPreviousState() {
      return true;
    }

    @Override
    public OrderState[] previousState() {
      return new OrderState[] {PAID};
    }

    @Override
    public OrderState[] nextState() {
      return new OrderState[] {DELIVERED};
    }

    @Override
    public boolean hasNextState() {
      return true;
    }
  },
  DELIVERED {
    @Override
    public boolean hasPreviousState() {
      return true;
    }

    @Override
    public OrderState[] previousState() {
      return new OrderState[] {SHIPPED};
    }

    @Override
    public OrderState[] nextState() {
      return new OrderState[] {};
    }

    @Override
    public boolean hasNextState() {
      return false;
    }
  },
  CANCELLED {
    @Override
    public boolean hasPreviousState() {
      return true;
    }

    @Override
    public OrderState[] previousState() {
      return new OrderState[] {PLACED, PAID};
    }

    @Override
    public OrderState[] nextState() {
      return new OrderState[] {};
    }

    @Override
    public boolean hasNextState() {
      return false;
    }
  };

  public abstract boolean hasPreviousState();

  public abstract OrderState[] previousState();

  public abstract boolean hasNextState();

  public abstract OrderState[] nextState();
}
