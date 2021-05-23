package io.realworld.backend.application.exception.config;

import io.realworld.backend.application.exception.InvalidRequestException;
import io.realworld.backend.application.exception.UserNotFoundException;
import io.realworld.backend.rest.api.GenericErrorModelData;
import io.realworld.backend.rest.api.GenericErrorModelErrorsData;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings("PMD.ExcessiveParameterList")
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      @Nullable Object body,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    GenericErrorModelData model = new GenericErrorModelData();
    GenericErrorModelErrorsData errors = new GenericErrorModelErrorsData();
    errors.setBody(List.of(ex.getMessage() == null ? ex.toString() : ex.getMessage()));
    model.setErrors(errors);
    // HACK status
    return new ResponseEntity<>(
        model, status == HttpStatus.BAD_REQUEST ? HttpStatus.UNPROCESSABLE_ENTITY : status);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request) {
    return handleExceptionInternal(
        ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<Object> handleInvalidRequestException(
      InvalidRequestException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, null, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Object> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {
    return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }
}
