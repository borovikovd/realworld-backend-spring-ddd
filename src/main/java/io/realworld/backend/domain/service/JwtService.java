package io.realworld.backend.domain.service;

import io.realworld.backend.domain.aggregate.user.User;
import java.util.Optional;

public interface JwtService {
  /** Generates JWT token for a given user. */
  String generateToken(User user);

  /** Finds a user that given token was generated for. */
  Optional<User> getUser(String token);
}
