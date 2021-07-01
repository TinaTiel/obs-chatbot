package com.tinatiel.obschatbot.data.client.obs.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Provides an user-presentable representation of OBS-related settings
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ObsClientSettingsDto extends OwnerDto {

  private String host;
  private int port;
  private String password;
  private long connectionTimeoutMs;

}
