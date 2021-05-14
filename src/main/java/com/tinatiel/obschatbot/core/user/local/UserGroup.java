package com.tinatiel.obschatbot.core.user.local;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Group that an ${@link LocalUser} may be assigned to, for example a "regulars"
 * group.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserGroup {

  private String name;
}
