/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import javax.net.ssl.SSLSocketFactory;
import org.pircbotx.PircBotX;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Generates new instances of PircBotX with the most recent Twitch Chat Settings.
 */
public class TwitchChatClientFactory implements ClientFactory<PircBotX, TwitchChatClientSettings> {

  private final OwnerService ownerService;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory;
  private final SSLSocketFactory sslSocketFactory;
  private final PircBotxListener pircBotxListener;

  /**
   * Create a new factory instance, using the specified ${@link ClientSettingsFactory} to get the
   * latest Twitch chat settings, PircBotxListener as registered listener, and SSL connection
   * factory.
   */
  public TwitchChatClientFactory(OwnerService ownerService,
    OAuth2AuthorizedClientService authorizedClientService,
    ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory,
    SSLSocketFactory sslSocketFactory,
    PircBotxListener pircBotxListener) {
    this.ownerService = ownerService;
    this.authorizedClientService = authorizedClientService;
    this.clientSettingsFactory = clientSettingsFactory;
    this.sslSocketFactory = sslSocketFactory;
    this.pircBotxListener = pircBotxListener;
  }

  @Override
  public TwitchChatClientDelegate generate() {

    // Get a fresh set of settings
    TwitchChatClientSettings settings = clientSettingsFactory.getSettings();
    String fooi = settings.getIrcHost();
    Integer bar = settings.getIrcPort();
    // Create a new bot with those settings
    org.pircbotx.Configuration.Builder builder = new org.pircbotx.Configuration.Builder()
      // Twitch's IRC url
      .addServer(settings.getIrcHost(), settings.getIrcPort())

      // Auto-join the broadcaster's channel
      .addAutoJoinChannel("#" + settings.getBroadcasterAccountUsername())

      // Name is required by PircBotX, but we can ignore it because it is derived from the token
      .setName("ignoreme");

      // IRC Login is 'oauth:' + the oauth user token
      OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
        "twitch", ownerService.getOwner().getName());
      if(authorizedClient != null) {
        builder.setServerPassword("oauth:" + authorizedClient.getAccessToken().getTokenValue());
      }

      // Register our listener so that we can manage client lifecycle and process chat messages
    builder
      .addListener(pircBotxListener)

      // Twitch does not support WHO
      .setOnJoinWhoEnabled(false)

      // Connection settings
      .setSocketFactory(sslSocketFactory)
      .setAutoReconnectAttempts(settings.getConnectionAttempts())
      .setSocketConnectTimeout(Long.valueOf(settings.getConnectionTimeoutMs()).intValue());

    // Build it!
    PircBotX bot = new PircBotX(builder.buildConfiguration());

    return new TwitchChatClientDelegate(bot, settings);
  }

}
