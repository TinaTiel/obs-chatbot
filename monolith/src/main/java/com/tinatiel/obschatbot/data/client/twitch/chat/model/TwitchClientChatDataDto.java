package com.tinatiel.obschatbot.data.client.twitch.chat.model;

import com.tinatiel.obschatbot.data.common.OwnerDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
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
  @NotBlank
  private String broadcasterChannelUsername;
  @Positive
  private long connectionTimeoutMs;
  @Positive
  private int connectionAttempts;

  private String joinMessage;
  private String leaveMessage;
  @NotBlank
  private String trigger;
  private boolean parseEntireMessage;
}
