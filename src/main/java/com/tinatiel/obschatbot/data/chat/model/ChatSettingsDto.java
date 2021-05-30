package com.tinatiel.obschatbot.data.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An user-presentable representation of global chat settings.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatSettingsDto {

  /**
   * What char should trigger a command, e.g. "!", etc
   */
  @Builder.Default
  private String trigger = "!";

  /**
   * If an entire chat message should be parsed for a command
   */
  private boolean parseEntireMessage;

  /**
   * When joining via chat, what message should be displayed on join.
   */
  @Builder.Default
  private String joinMessage = "OBS Chatbot is ready! Type !help to see available commands";

  /**
   * When joining via chat, what message should be displayed before parting.
   */
  @Builder.Default
  private String leaveMessage = "OBS Chatbot is shutting down";

}
