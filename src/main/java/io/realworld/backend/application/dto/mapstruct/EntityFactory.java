package io.realworld.backend.application.dto.mapstruct;

import io.realworld.backend.rest.api.UserData;

public class EntityFactory {
  public UserData createUserData() {
    return new UserData();
  }
}
