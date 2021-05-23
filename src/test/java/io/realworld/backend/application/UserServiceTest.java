package io.realworld.backend.application;

import static io.realworld.backend.application.Util.validateBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import io.realworld.backend.application.exception.EmailAlreadyUsedException;
import io.realworld.backend.application.exception.InvalidPasswordException;
import io.realworld.backend.application.exception.UsernameAlreadyUsedException;
import io.realworld.backend.application.service.UserService;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.aggregate.user.UserRepository;
import io.realworld.backend.domain.service.AuthenticationService;
import io.realworld.backend.domain.service.JwtService;
import io.realworld.backend.rest.api.LoginUserData;
import io.realworld.backend.rest.api.LoginUserRequestData;
import io.realworld.backend.rest.api.NewUserData;
import io.realworld.backend.rest.api.NewUserRequestData;
import io.realworld.backend.rest.api.UpdateUserData;
import io.realworld.backend.rest.api.UpdateUserRequestData;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class UserServiceTest {
  private @MonotonicNonNull UserService userService;
  @Mock private @MonotonicNonNull UserRepository userRepository;
  @Mock private @MonotonicNonNull JwtService jwtService;
  @Mock private @MonotonicNonNull AuthenticationService authenticationService;

  @BeforeEach
  @RequiresNonNull({"userRepository", "jwtService", "authenticationService"})
  public void setUp() {
    openMocks(this);
    given(authenticationService.getCurrentUser())
        .willReturn(Optional.of(new User("email@example.com", "example", "hash")));
    given(authenticationService.getCurrentToken()).willReturn(Optional.of("token"));
    given(jwtService.generateToken(any())).willReturn("token");
    userService = new UserService(userRepository, jwtService, authenticationService);
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testCreateUser_normal() {
    final var newUser = new User("email@example.com", "example", "hash");
    newUser.setId(1);

    given(userRepository.save(any())).willReturn(newUser);
    final var resp = userService.createUser(newUser("email@example.com", "example", "123"));
    final var body = validateBody(resp);
    final var user = body.getUser();

    assertThat(user.getEmail()).isEqualTo("email@example.com");
    assertThat(user.getToken()).isNotBlank();

    verify(userRepository, times(1)).save(any());
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testCreateUser_email_exists() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user));
    final var userService1 = this.userService;
    assertThrows(
        EmailAlreadyUsedException.class,
        () -> {
          userService1.createUser(newUser("email@example.com", "example", "123"));
        });
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testCreateUser_username_exists() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByUsername("example")).willReturn(Optional.of(user));
    final var userService = this.userService;
    assertThrows(
        UsernameAlreadyUsedException.class,
        () -> {
          userService.createUser(newUser("email@example.com", "example", "123"));
        });
  }

  @Test
  @RequiresNonNull({"userRepository", "userService", "authenticationService", "jwtService"})
  public void testLogin_normal() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user));
    given(authenticationService.authenticate("email@example.com", "123"))
        .willReturn(Optional.of(user));
    given(jwtService.generateToken(user)).willReturn("token");

    final var resp = userService.login(loginUser("email@example.com", "123"));
    final var body = validateBody(resp);
    final var userWithToken = body.getUser();
    assertThat(userWithToken.getEmail()).isEqualTo(user.getEmail());
    assertThat(userWithToken.getToken()).isEqualTo("token");
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testLogin_password_doesnt_match() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user));

    final var userService1 = userService;
    assertThrows(
        InvalidPasswordException.class,
        () -> {
          userService1.login(loginUser("email@example.com", "321"));
        });
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testGetCurrentUser() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user));

    final var reps = userService.getCurrentUser();
    final var userWithToken = validateBody(reps).getUser();
    assertThat(castNonNull(userWithToken.getEmail())).isEqualTo(user.getEmail());
    assertThat(userWithToken.getToken()).isNotBlank();
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testUpdateUser_normal() {
    final var user = new User("email@example.com", "example", "hash");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user));

    final var resp = userService.updateCurrentUser(updateUser(null, null, "bio", "image"));
    final var updatedUserWithToken = validateBody(resp).getUser();
    assertThat(castNonNull(updatedUserWithToken.getBio())).isEqualTo("bio");
    assertThat(castNonNull(updatedUserWithToken.getImage())).isEqualTo("image");
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testUpdateUser_username_exists() {
    final var user1 = new User("email@example.com", "example", "hash1");
    final var user2 = new User("email2@example.com", "example2", "hash2");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user1));
    given(userRepository.findByUsername("example2")).willReturn(Optional.of(user2));

    final var userService1 = userService;
    assertThrows(
        UsernameAlreadyUsedException.class,
        () -> {
          userService1.updateCurrentUser(updateUser("example2", null, null, null));
        });
  }

  @Test
  @RequiresNonNull({"userRepository", "userService"})
  public void testUpdateUser_email_exists() {
    final var user1 = new User("email@example.com", "example", "hash1");
    final var user2 = new User("email2@example.com", "example2", "hash2");
    given(userRepository.findByEmail("email@example.com")).willReturn(Optional.of(user1));
    given(userRepository.findByEmail("email2@example.com")).willReturn(Optional.of(user2));

    final var userService1 = userService;
    assertThrows(
        EmailAlreadyUsedException.class,
        () -> {
          userService1.updateCurrentUser(updateUser(null, "email2@example.com", null, null));
        });
  }

  private NewUserRequestData newUser(String email, String username, String password) {
    final var newUserData = new NewUserData();
    newUserData.setEmail(email);
    newUserData.setUsername(username);
    newUserData.setPassword(password);
    final var requestData = new NewUserRequestData();
    requestData.setUser(newUserData);
    return requestData;
  }

  private LoginUserRequestData loginUser(String email, String password) {
    final var loginUserData = new LoginUserData();
    loginUserData.setEmail(email);
    loginUserData.setPassword(password);

    final var requestData = new LoginUserRequestData();
    requestData.setUser(loginUserData);
    return requestData;
  }

  private UpdateUserRequestData updateUser(
      @Nullable String username,
      @Nullable String email,
      @Nullable String bio,
      @Nullable String image) {
    final var updateUser = new UpdateUserData();
    if (username != null) {
      updateUser.setUsername(username);
    }
    if (email != null) {
      updateUser.setEmail(email);
    }
    if (bio != null) {
      updateUser.setBio(bio);
    }
    if (image != null) {
      updateUser.setImage(image);
    }
    final var requestData = new UpdateUserRequestData();
    requestData.setUser(updateUser);
    return requestData;
  }
}
