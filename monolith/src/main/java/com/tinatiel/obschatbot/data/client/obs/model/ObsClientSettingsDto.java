package com.tinatiel.obschatbot.data.client.obs.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import com.tinatiel.obschatbot.web.error.PortRange;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

  @NotBlank
  private String host;
  @PortRange
  private int port;
  private String password;
  @Positive
  private long connectionTimeoutMs;

}
