/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Encompasses an actor in this system; what identifies them, and what permissions they have.
 */
@AllArgsConstructor
@Getter
@Builder
@ToString
public class User {

  private final String id;

  private final Platform platform;

  private final String username;

  @Builder.Default
  private final UserSecurityDetails userSecurityDetails = UserSecurityDetails.builder().build();

  @Builder.Default
  private final Set<LocalGroupDto> groups = new HashSet<>();

  /**
   * Returns the SYSTEM user; a Local user that has admin (broadcaster) permissions.
   */
  public static User systemUser() {
    return User.builder()
      .platform(Platform.LOCAL)
      .username("INTERNAL") // TODO: Should this match what ownerService returns?
      .userSecurityDetails(UserSecurityDetails.builder().broadcaster(true).build())
      .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return platform == user.platform && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(platform, username);
  }

}
