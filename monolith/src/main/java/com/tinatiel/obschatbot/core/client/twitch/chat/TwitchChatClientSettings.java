/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Encompasses the set of information required to connect to Twitch IRC.
 */

@Builder
@AllArgsConstructor
@Data
@ToString
public class TwitchChatClientSettings {

  private String ircHost;
  private int ircPort;
  private String broadcasterAccountUsername;
  private long connectionTimeoutMs;
  private int connectionAttempts;
  private String joinMessage;
  private String leaveMessage;
  private String trigger;
  private boolean parseEntireMessage;

}
