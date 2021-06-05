package com.tinatiel.obschatbot.data.system.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An user-presentable representation of global system settings.
 */
@Entity
@Table(name = "settings_system")
@NoArgsConstructor
@Data
public class SystemSettingsEntity extends OwnerEntity {

  /**
   * Maximum number of actions executed at once in a batch
   */
  private int maxActionBatchSize = 3; // RequestSchedulerConfig

  /**
   * Milliseconds timeout for expanding a Command into actions; failsafe
   * in the case when cycle detection fails.
   */
  private int recursionTimeoutMillis = 1000; // CommandExpanderConfig;

}
