package com.tinatiel.obschatbot.data.localuser.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
  @NotNull
  private UUID id;
  @NotBlank
  private String name;
}
