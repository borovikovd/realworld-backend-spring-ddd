package io.realworld.backend.domain.aggregate.user;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByEmail(String username);

  Optional<User> findByUsername(String username);
}
