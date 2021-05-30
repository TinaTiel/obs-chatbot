package com.tinatiel.obschatbot.data.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An user-presentable representation of global system settings.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SystemSettingsDto {

  /**
   * Maximum number of actions executed at once in a batch
   */
  @Builder.Default
  private int maxActionBatchSize = 3; // RequestSchedulerConfig

  /**
   * Milliseconds timeout for expanding a Command into actions; failsafe
   * in the case when cycle detection fails.
   */
  @Builder.Default
  private int recursionTimeoutMillis = 1000; // CommandExpanderConfig

}
