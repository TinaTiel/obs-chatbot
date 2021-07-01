package com.tinatiel.obschatbot.data.system.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * An user-presentable representation of global system settings.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class SystemSettingsDto extends OwnerDto {

  /**
   * Maximum number of actions executed at once in a batch
   */
  private int maxActionBatchSize;

  /**
   * Milliseconds timeout for expanding a Command into actions; failsafe
   * in the case when cycle detection fails.
   */
  private int recursionTimeoutMillis;

}
