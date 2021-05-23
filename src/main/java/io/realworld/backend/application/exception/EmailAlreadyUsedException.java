package io.realworld.backend.application.exception;

public class EmailAlreadyUsedException extends InvalidRequestException {

  public EmailAlreadyUsedException(String message) {
    super(message);
  }
}
