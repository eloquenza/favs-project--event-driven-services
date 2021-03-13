package edu.hsh.favs.project.escqrs.services.commons.exceptions;

public class EntityNotFoundException extends BusinessException {

  public EntityNotFoundException(String message) {
    super(message);
  }
}
