package io.realworld.backend.application.dto.mapstruct;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.realworld.backend.domain.aggregate.user.User;
import org.junit.jupiter.api.Test;

public class UserMapperTest {
  @Test
  public void testMapUser() {
    User u = new User("ddd@dd.com", "dddd", "123");
    final var d = UserMapper.INSTANCE.toUserData(u, "token");
    assertNotNull(d);
  }
}
