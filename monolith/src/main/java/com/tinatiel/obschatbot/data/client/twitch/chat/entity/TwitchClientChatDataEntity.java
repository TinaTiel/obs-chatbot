package com.tinatiel.obschatbot.data.client.twitch.chat.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class TwitchClientChatDataEntity extends OwnerEntity {
  private String broadcasterChannelUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;

  /**
   * What char should trigger a command, e.g. "!", etc
   */
  private String trigger = "!";

  /**
   * If an entire chat message should be parsed for a command
   */
  private boolean parseEntireMessage;

  /**
   * When joining via chat, what message should be displayed on join.
   */
  private String joinMessage; // = "OBS Chatbot is ready! Type !help to see available commands";

  /**
   * When joining via chat, what message should be displayed before parting.
   */
  private String leaveMessage; // = "OBS Chatbot is shutting down";

}
