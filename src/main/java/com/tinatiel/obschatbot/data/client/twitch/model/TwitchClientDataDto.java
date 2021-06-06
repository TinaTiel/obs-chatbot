package com.tinatiel.obschatbot.data.client.twitch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Provides an user-presentable representation of Twitch-related settings
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TwitchClientDataDto {
  private String clientId;
  private String clientSecret;
  private String botAccountUsername;
  private String broadcasterChannelUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;
}
