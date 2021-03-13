package edu.hsh.favs.project.escqrs.services.commons.exceptions;

public class IllegalEntityOperationException extends BusinessException {

  public IllegalEntityOperationException(String message) {
    super(message);
  }
}
