/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * A ClientSettingsFactory implementation that returns the latest ${@link
 * TwitchChatClientSettings}.
 */
@Slf4j
@Setter
@ConfigurationProperties("com.tinatiel.twitch.chat")
@Configuration
public class TwitchChatClientSettingsFactory implements
    ClientSettingsFactory<TwitchChatClientSettings> {

  @Setter(AccessLevel.NONE)
  private final OwnerService ownerService;
  @Setter(AccessLevel.NONE)
  private final TwitchClientChatDataService dataService;

  private String ircHost;
  private Integer ircPort;

  public TwitchChatClientSettingsFactory(
    OwnerService ownerService,
    TwitchClientChatDataService dataService) {
    this.ownerService = ownerService;
    this.dataService = dataService;
  }

  @Override
  public TwitchChatClientSettings getSettings() {

    TwitchChatClientSettings.TwitchChatClientSettingsBuilder builder = TwitchChatClientSettings
      .builder()
      .ircHost(ircHost)
      .ircPort(ircPort);

    dataService.findByOwner(ownerService.getOwner().getId()).ifPresent(data -> {
      builder
        .broadcasterAccountUsername(data.getBroadcasterChannelUsername())
        .connectionAttempts(data.getConnectionAttempts())
        .connectionTimeoutMs(data.getConnectionTimeoutMs())
        .joinMessage(data.getJoinMessage())
        .leaveMessage(data.getLeaveMessage())
        .trigger(data.getTrigger())
        .parseEntireMessage(data.isParseEntireMessage());
    });

    return builder.build();

  }
}
