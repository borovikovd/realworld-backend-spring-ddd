package io.realworld.backend.application.exception;

public class ArticleNotFoundException extends InvalidRequestException {

  public ArticleNotFoundException(String message) {
    super(message);
  }
}
