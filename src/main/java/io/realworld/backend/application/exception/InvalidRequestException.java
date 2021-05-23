package io.realworld.backend.application.exception;

public abstract class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }
}
