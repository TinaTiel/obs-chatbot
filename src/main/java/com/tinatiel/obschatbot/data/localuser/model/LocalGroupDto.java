package com.tinatiel.obschatbot.data.localuser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Group that an ${@link LocalUserDto} may be assigned to, for example a "regulars"
 * group.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocalGroupDto {
  private String name;
}
