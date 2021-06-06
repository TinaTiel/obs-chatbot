package com.tinatiel.obschatbot.data.client.twitch.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Provides an user-presentable representation of Twitch-related settings
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class TwitchClientDataDto extends OwnerDto {
  private String clientId;
  private String clientSecret;
  private String botAccountUsername;
  private String broadcasterChannelUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;
}
