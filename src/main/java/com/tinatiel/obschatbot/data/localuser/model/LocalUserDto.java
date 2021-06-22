package com.tinatiel.obschatbot.data.localuser.model;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.OwnerDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Stores the most minimal information required to associate an user with a locally-created group
 * (for example, adding specific subscribers to a 'special' group). Does not store other information
 * (follower status, subscribe $$$, etc.) both for GDPR reasons and because this information is very
 * transient and will change during a broadcast.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class LocalUserDto extends OwnerDto {
  private Platform platform;
  private String username;
  @Builder.Default
  private List<LocalGroupDto> groups = new ArrayList<>();
  private boolean broadcaster;
}
