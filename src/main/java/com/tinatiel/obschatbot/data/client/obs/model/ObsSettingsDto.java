package com.tinatiel.obschatbot.data.client.obs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Provides an user-presentable representation of OBS-related settings
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ObsSettingsDto {

  @Builder.Default
  private String host = "localhost";

  @Builder.Default
  private int port = 4444;

  @ToString.Exclude
  private String password;

  @Builder.Default
  private long connectionTimeoutMs = 1000;

}
