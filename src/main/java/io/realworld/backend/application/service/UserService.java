package io.realworld.backend.application.service;

import static io.realworld.backend.application.dto.Mappers.toUserResponse;

import io.realworld.backend.application.dto.Mappers;
import io.realworld.backend.application.exception.EmailAlreadyUsedException;
import io.realworld.backend.application.exception.InvalidPasswordException;
import io.realworld.backend.application.exception.UserNotFoundException;
import io.realworld.backend.application.exception.UsernameAlreadyUsedException;
import io.realworld.backend.application.util.BaseService;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.aggregate.user.UserRepository;
import io.realworld.backend.domain.service.AuthenticationService;
import io.realworld.backend.domain.service.JwtService;
import io.realworld.backend.rest.api.LoginUserRequestData;
import io.realworld.backend.rest.api.NewUserRequestData;
import io.realworld.backend.rest.api.UpdateUserRequestData;
import io.realworld.backend.rest.api.UserApiDelegate;
import io.realworld.backend.rest.api.UserResponseData;
import io.realworld.backend.rest.api.UsersApiDelegate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

@Service
@Transactional
public class UserService extends BaseService implements UserApiDelegate, UsersApiDelegate {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationService authenticationService;

  /** Creates ApiFacade instance. */
  @Autowired
  public UserService(
      UserRepository userRepository,
      JwtService jwtService,
      AuthenticationService authenticationService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.authenticationService = authenticationService;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<UserResponseData> createUser(NewUserRequestData req) {
    final var newUserData = req.getUser();
    String username = newUserData.getUsername();
    String email = newUserData.getEmail();
    userRepository
        .findByUsername(username)
        .ifPresent(
            u -> {
              throw new UsernameAlreadyUsedException("Username already used - " + username);
            });
    userRepository
        .findByEmail(email)
        .ifPresent(
            u -> {
              throw new EmailAlreadyUsedException("Email already used - " + email);
            });
    final var newUser =
        new User(email, username, authenticationService.encodePassword(newUserData.getPassword()));
    final var user = userRepository.save(newUser);
    return ok(toUserResponse(user, jwtService.generateToken(user)));
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<UserResponseData> getCurrentUser() {
    return authenticationService
        .getCurrentUser()
        .map(u -> ok(toUserResponse(u, authenticationService.getCurrentToken().orElse(""))))
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<UserResponseData> updateCurrentUser(UpdateUserRequestData req) {
    final var user =
        authenticationService
            .getCurrentUser()
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    final var update = req.getUser();
    final var email = update.getEmail();
    if (email != null && !email.equals(user.getEmail())) {
      userRepository
          .findByEmail(email)
          .ifPresent(
              u -> {
                throw new EmailAlreadyUsedException("Email already used - " + email);
              });
    }
    final var username = update.getUsername();
    if (username != null && !username.equals(user.getUsername())) {
      userRepository
          .findByUsername(username)
          .ifPresent(
              u -> {
                throw new UsernameAlreadyUsedException("Username already used - " + username);
              });
    }
    Mappers.updateUser(user, update);

    return ok(toUserResponse(user, authenticationService.getCurrentToken().orElse("")));
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<UserResponseData> login(LoginUserRequestData body) {
    final var loginUserData = body.getUser();
    final var email = loginUserData.getEmail();
    return authenticationService
        .authenticate(loginUserData.getEmail(), loginUserData.getPassword())
        .map(u -> ok(toUserResponse(u, jwtService.generateToken(u))))
        .orElseThrow(() -> new InvalidPasswordException("Can not authenticate - " + email));
  }

  /** {@inheritDoc} */
  @Override
  public AuthenticationService getAuthenticationService() {
    return authenticationService;
  }
}
