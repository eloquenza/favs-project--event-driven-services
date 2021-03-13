package edu.hsh.favs.project.escqrs.services.commons.web.errorhandling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.web.context.request.WebRequest;

public class ErrorResponse {

  private final int status;
  private final String httpStatusName;
  private final String message;
  private final Date timestamp;
  private WebRequest request;
  private List<ValidationError> errors;

  public ErrorResponse(int status, String httpStatusName, String message) {
    this.status = status;
    this.httpStatusName = httpStatusName;
    this.message = message;
    this.timestamp = new Date();
  }

  public ErrorResponse(int status, String httpStatusName, String message, WebRequest request) {
    this(status, httpStatusName, message);
    this.request = request;
  }

  public int getStatus() {
    return status;
  }

  public String getHttpStatusName() {
    return httpStatusName;
  }

  public String getMessage() {
    return message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public WebRequest getRequest() {
    return request;
  }

  public ErrorResponse setRequest(WebRequest request) {
    this.request = request;
    return this;
  }

  public void addValidationError(String field, String message) {
    if (Objects.isNull(errors)) {
      errors = new ArrayList<>();
    }
    errors.add(new ValidationError(field, message));
  }
}
