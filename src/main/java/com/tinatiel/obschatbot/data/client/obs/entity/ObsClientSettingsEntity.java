package com.tinatiel.obschatbot.data.client.obs.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import javax.persistence.Entity;
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
@Entity
public class ObsClientSettingsEntity extends OwnerEntity {

  private String host;
  private int port;
  private String password;
  private long connectionTimeoutMs;

}
