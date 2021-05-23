package io.realworld.backend.application.service;

import static io.realworld.backend.application.dto.Mappers.toProfileResponse;

import io.realworld.backend.application.exception.UserNotFoundException;
import io.realworld.backend.application.util.BaseService;
import io.realworld.backend.domain.aggregate.follow.FollowRelation;
import io.realworld.backend.domain.aggregate.follow.FollowRelationId;
import io.realworld.backend.domain.aggregate.follow.FollowRelationRepository;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.aggregate.user.UserRepository;
import io.realworld.backend.domain.service.AuthenticationService;
import io.realworld.backend.rest.api.ProfileResponseData;
import io.realworld.backend.rest.api.ProfilesApiDelegate;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService extends BaseService implements ProfilesApiDelegate {
  private final UserRepository userRepository;
  private final FollowRelationRepository followRelationRepository;
  private final AuthenticationService authenticationService;

  /** Creates ProfileService instance. */
  @Autowired
  public ProfileService(
      UserRepository userRepository,
      FollowRelationRepository followRelationRepository,
      AuthenticationService authenticationService) {
    this.userRepository = userRepository;
    this.followRelationRepository = followRelationRepository;
    this.authenticationService = authenticationService;
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<ProfileResponseData> followUserByUsername(String username) {
    final var currentUser = currentUserOrThrow();

    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    final var followRelationId = new FollowRelationId(currentUser.getId(), user.getId());
    followRelationRepository
        .findById(followRelationId)
        .or(
            () -> {
              final var followRelation = new FollowRelation(currentUser.getId(), user.getId());
              followRelationRepository.save(followRelation);
              return Optional.of(followRelation);
            });

    return ok(toProfileResponse(user, true));
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<ProfileResponseData> getProfileByUsername(String username) {
    final var currentUser = authenticationService.getCurrentUser();
    Predicate<User> isFollowing =
        (u) ->
            currentUser
                .map(
                    cu ->
                        followRelationRepository
                            .findById(new FollowRelationId(cu.getId(), u.getId()))
                            .isPresent())
                .orElse(false);
    return userRepository
        .findByUsername(username)
        .map(u -> ok(toProfileResponse(u, isFollowing.test(u))))
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<ProfileResponseData> unfollowUserByUsername(String username) {
    final var currentUser = currentUserOrThrow();

    final var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    final var followRelationId = new FollowRelationId(currentUser.getId(), user.getId());
    followRelationRepository.deleteById(followRelationId);

    return ok(toProfileResponse(user, false));
  }

  /** {@inheritDoc} */
  @Override
  public AuthenticationService getAuthenticationService() {
    return authenticationService;
  }
}
