package edu.hsh.favs.project.escqrs.services.commons.web.errorhandling;

/**
 * This class represents a POJO to be (de)serialized into a JSON containing information about an
 * unprocessable entity during a HTTP request. It is simply used to type this information into an
 * object, such that the ErrorResponse class is able to create a typed ArrayList containing all
 * errors that might have happened during the entity validation.
 */
public class ValidationError {
  private final String field;
  private final String message;

  public ValidationError(String field, String message) {
    this.field = field;
    this.message = message;
  }

  public String getField() {
    return field;
  }

  public String getMessage() {
    return message;
  }
}
