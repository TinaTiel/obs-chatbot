/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import javax.net.ssl.SSLSocketFactory;
import org.pircbotx.PircBotX;

/**
 * Generates new instances of PircBotX with the most recent Twitch Chat Settings.
 */
public class TwitchChatClientFactory implements ClientFactory<PircBotX, TwitchChatClientSettings> {

  private final ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory;
  private final SSLSocketFactory sslSocketFactory;
  private final PircBotxListener pircBotxListener;

  /**
   * Create a new factory instance, using the specified ${@link ClientSettingsFactory} to get the
   * latest Twitch chat settings, PircBotxListener as registered listener, and
   * SSL connection factory.
   */
  public TwitchChatClientFactory(
      ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory,
      SSLSocketFactory sslSocketFactory,
      PircBotxListener pircBotxListener) {
    this.clientSettingsFactory = clientSettingsFactory;
    this.sslSocketFactory = sslSocketFactory;
    this.pircBotxListener = pircBotxListener;
  }

  @Override
  public TwitchChatClientDelegate generate() {

    // Get a fresh set of settings
    TwitchChatClientSettings settings = clientSettingsFactory.getSettings();

    // Create a new bot with those settings
    PircBotX bot = new PircBotX(new org.pircbotx.Configuration.Builder()
        // Twitch's IRC url
        .addServer(settings.getHost(), settings.getPort())

        // Auto-join the broadcaster's channel
        .addAutoJoinChannel("#" + settings.getBroadcasterChannel())

        // Name is required by PircBotX, but we can ignore it because it is derived from the token
        .setName("ignore")

        // IRC Login is 'oauth:' + the oauth user token
        .setServerPassword("oauth:" + settings.getOauthUserToken())

        // Register our listener so that we can manage client lifecycle and process chat messages
        .addListener(pircBotxListener)

        // Twitch does not support WHO
        .setOnJoinWhoEnabled(false)

        // Connection settings
        .setSocketFactory(sslSocketFactory)
        .setAutoReconnectAttempts(settings.getConnectionAttempts())
        .setSocketConnectTimeout(Long.valueOf(settings.getConnectionTimeoutMs()).intValue())

        // Build it!
        .buildConfiguration()
    );

    return new TwitchChatClientDelegate(bot, settings);
  }

}
