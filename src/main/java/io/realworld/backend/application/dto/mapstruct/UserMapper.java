package io.realworld.backend.application.dto.mapstruct;

import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.rest.api.UserData;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = EntityFactory.class)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(target = "user.username", source = "u.username")
  @Mapping(target = "user.email", source = "u.email")
  @Mapping(target = "user.bio", source = "u.bio")
  @Mapping(target = "user.image", source = "u.image")
  @Mapping(target = "user.token", source = "token")
  UserData toUserData(User u, @Nullable String token);
}
