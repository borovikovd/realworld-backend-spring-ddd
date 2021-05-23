package io.realworld.backend.application.exception;

public class InvalidPasswordException extends InvalidRequestException {
  public InvalidPasswordException(String message) {
    super(message);
  }
}
