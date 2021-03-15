package edu.hsh.favs.project.escqrs.services.commons.web.errorhandling;

import edu.hsh.favs.project.escqrs.services.commons.exceptions.BusinessException;
import edu.hsh.favs.project.escqrs.services.commons.exceptions.EntityNotFoundException;
import edu.hsh.favs.project.escqrs.services.commons.exceptions.IllegalEntityOperationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import reactor.util.Logger;
import reactor.util.Loggers;

/**
 * This class provides our microservices with a centralized, shared exception handling mechanism.
 * Each microservice wants to use this global exception handler in order to make sure that our
 * exceptions will be handled exactly the same by each service. To do so, each microservice needs to
 * derive a sub class from this class, and add the annotation @RestControllerAdvice. This allows the
 * Spring Boot framework to pick up this class and register it as a ExceptionHandler for each
 * response.
 */
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final Logger log = Loggers.getLogger(GlobalExceptionHandler.class.getName());

  @ExceptionHandler(IllegalEntityOperationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleIllegalEntityOperationException(
      IllegalEntityOperationException ex) {
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
    return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.toString(),
            "Validation error. Check 'errors' field for details. Concrete exception message: "
                + ex.getMessage());
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return ResponseEntity.unprocessableEntity().body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleAllUncaughtException(Exception exception) {
    return buildErrorResponse(
        exception,
        "Unknown error occurred: " + exception.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<Object> buildErrorResponse(Exception exception, HttpStatus httpStatus) {
    ErrorResponse errorResponse =
        new ErrorResponse(httpStatus.value(), httpStatus.toString(), exception.getMessage());
    return constructResponseEntity(exception, httpStatus, errorResponse);
  }

  private ResponseEntity<Object> buildErrorResponse(
      Exception exception, String message, HttpStatus httpStatus) {
    ErrorResponse errorResponse =
        new ErrorResponse(httpStatus.value(), httpStatus.toString(), message);
    return constructResponseEntity(exception, httpStatus, errorResponse);
  }

  private ResponseEntity<Object> buildErrorResponse(
      Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
    ErrorResponse errorResponse =
        new ErrorResponse(httpStatus.value(), httpStatus.toString(), message, request);
    return constructResponseEntity(exception, httpStatus, errorResponse);
  }

  private ResponseEntity<Object> constructResponseEntity(
      Exception ex, HttpStatus httpStatus, ErrorResponse errorResponse) {
    log.info("Exception happened:");
    log.info("\t\t" + ex);
    log.info("Logging response sent to client: " + errorResponse);
    return ResponseEntity.status(httpStatus).body(errorResponse);
  }

  @Override
  public @NotNull ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildErrorResponse(ex, ex.getMessage(), status, request);
  }
}
