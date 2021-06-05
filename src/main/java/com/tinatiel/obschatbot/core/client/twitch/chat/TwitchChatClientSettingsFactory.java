/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * A ClientSettingsFactory implementation that returns the latest ${@link
 * TwitchChatClientSettings}.
 */
@Slf4j
public class TwitchChatClientSettingsFactory implements
    ClientSettingsFactory<TwitchChatClientSettings> {

  private final OwnerService ownerService;
  private final TwitchChatClientSettings settings;
  private final OAuth2AuthorizedClientService clientService;

  public TwitchChatClientSettingsFactory(
    OwnerService ownerService,
    TwitchChatClientSettings settings,
    OAuth2AuthorizedClientService clientService) {
    this.ownerService = ownerService;
    this.settings = settings;
    this.clientService = clientService;
  }

  @Override
  public TwitchChatClientSettings getSettings() {
    // Load the (authorized) OAuth2 client
    OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(
        "twitch", ownerService.getOwner().getName());

    if (authorizedClient != null) {
      settings.setOauthUserToken(authorizedClient.getAccessToken().getTokenValue());
    } else {
      // TODO revisit, see if we can alert / respond better than logging
      log.warn("No Authorized Client found for Twitch + SYSTEM user");
    }
    return settings;
  }
}
