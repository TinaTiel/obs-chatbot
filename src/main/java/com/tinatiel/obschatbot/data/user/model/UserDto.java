package com.tinatiel.obschatbot.data.user.model;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.local.UserGroup;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An user-presentable representation of an user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

  private Platform platform;
  private String username;
  @Builder.Default
  private Set<UserGroup> groups = new HashSet<>();
  private boolean broadcaster;

}
