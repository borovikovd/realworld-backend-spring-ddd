package io.realworld.backend.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Util {
  public static <T> T validateBody(ResponseEntity<T> resp) {
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var body = resp.getBody();
    if (body == null) {
      throw new IllegalStateException();
    }
    return body;
  }
}
