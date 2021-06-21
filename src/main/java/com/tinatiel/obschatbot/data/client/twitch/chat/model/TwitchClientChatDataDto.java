package com.tinatiel.obschatbot.data.client.twitch.chat.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class TwitchClientChatDataDto extends OwnerDto {
  private String broadcasterChannelUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;
  private String joinMessage;
  private String leaveMessage;
  private String trigger;
  private boolean parseEntireMessage;
}
