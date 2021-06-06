package com.tinatiel.obschatbot.data.client.twitch.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class TwitchClientDataEntity extends OwnerEntity {
  private String clientId;
  private String clientSecret;
  private String botAccountUsername;
  private String broadcasterChannelUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;
}
