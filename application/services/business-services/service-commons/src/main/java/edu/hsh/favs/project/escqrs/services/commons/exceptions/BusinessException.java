package edu.hsh.favs.project.escqrs.services.commons.exceptions;

public class BusinessException extends RuntimeException {

  public BusinessException(String message) {
    super(message);
  }
}
