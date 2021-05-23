package io.realworld.backend.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.aggregate.user.UserRepository;
import io.realworld.backend.domain.service.JwtService;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JJwtService implements JwtService {
  private final String secret;
  private final int sessionTime;
  private final UserRepository userRepository;

  /**
   * Creates DefaultJwtService instance.
   *
   * @param secret jwt secret
   * @param sessionTime jwt session time in seconds
   * @param userRepository user repository
   */
  @Autowired
  public JJwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.sessionTime}") int sessionTime,
      UserRepository userRepository) {
    this.secret = secret;
    this.sessionTime = sessionTime;
    this.userRepository = userRepository;
  }

  /** {@inheritDoc} */
  @Override
  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(Long.toString(user.getId()))
        .setExpiration(new Date(System.currentTimeMillis() + sessionTime * 1000))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<User> getUser(String token) {
    try {
      final var subject =
          Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
      final var userId = Long.parseLong(subject);
      return userRepository.findById(userId);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
