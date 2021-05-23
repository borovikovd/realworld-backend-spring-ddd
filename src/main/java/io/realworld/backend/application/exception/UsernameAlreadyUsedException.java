package io.realworld.backend.application.exception;

public class UsernameAlreadyUsedException extends InvalidRequestException {

  public UsernameAlreadyUsedException(String message) {
    super(message);
  }
}
