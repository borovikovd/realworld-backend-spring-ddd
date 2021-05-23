package io.realworld.backend.application.exception;

public class UserNotFoundException extends InvalidRequestException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
