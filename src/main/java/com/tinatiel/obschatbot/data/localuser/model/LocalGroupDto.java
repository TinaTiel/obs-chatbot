package com.tinatiel.obschatbot.data.localuser.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents a Group that an ${@link LocalUserDto} may be assigned to, for example a "regulars"
 * group.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocalGroupDto extends OwnerDto {
  private UUID id;
  private String name;
}
