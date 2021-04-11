/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Encompasses the set of information required to connect to Twitch IRC.
 */

@AllArgsConstructor
@Data
@ToString
public class TwitchChatClientSettings {

  public static final String DEFAULT_HOST = "irc.chat.twitch.tv";
  public static final int DEFAULT_PORT = 6697;

  private String host;
  private int port;
  private String username;
  @ToString.Exclude
  private String oauthUserToken;
  private String broadcasterChannel;
  private long connectionTimeoutMs;
  private int connectionAttempts;
  private String joinMessage;
  private String leaveMessage;

}
