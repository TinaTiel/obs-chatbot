/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchChatClientMessagingConfig;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import javax.net.ssl.SSLSocketFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Encompasses all configuration for the Twitch IRC chat client.
 */
@Import(TwitchChatClientMessagingConfig.class)
@Configuration
public class TwitchChatClientConfig {

  @Autowired
  ChatRequestHandler chatRequestHandler;
  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;
  @Autowired
  TwitchClientLifecycleGateway twitchClientLifecycleGateway;
  @Autowired
  OwnerService ownerService;

  @Value("${TWITCH_TARGET_CHANNEL:noauth}")
  private String targetChannel;
  @Value("${TWITCH_USER:noauth}")
  private String twitchUsername;

  /**
   * Until we have this stored in a Repository, just hard-code it here.
   */
  @Bean
  ClientSettingsFactory<TwitchChatClientSettings> twitchChatClientSettingsFactory() {
    TwitchChatClientSettings settings = new TwitchChatClientSettings(
        TwitchChatClientSettings.DEFAULT_HOST, TwitchChatClientSettings.DEFAULT_PORT,
        twitchUsername, "foo", targetChannel,
        1000, 1,
        "OBS Chatbot is ready! Type !help to see available commands",
        "OBS Chatbot is shutting down"
    );
    return new TwitchChatClientSettingsFactory(ownerService, settings, authorizedClientService);
  }

  @Bean
  SSLSocketFactory sslSocketFactory() {
    return new UtilSSLSocketFactory();
  }

  @Bean
  PircBotxListener pircBotxListener() {
    return new PircBotxListener(
        twitchClientLifecycleGateway,
        chatRequestHandler,
        new TwitchChatClientTagsParser()
    );
  }

  @Bean
  ClientFactory<PircBotX, TwitchChatClientSettings> twitchChatClientFactory() {
    return new TwitchChatClientFactory(
      twitchChatClientSettingsFactory(),
      sslSocketFactory(),
      pircBotxListener()
    );
  }

  @Bean
  ActionCommandConsumer<TwitchChatClientDelegate> twitchChatClientActionCommandConsumer() {
    return new TwitchChatActionCommandConsumer(twitchClientLifecycleGateway);
  }

  @Bean
  ClientManager twitchChatClientManager() {
    return new TwitchChatClientManager(
      twitchClientLifecycleGateway,
      twitchChatClientFactory(),
      twitchChatClientActionCommandConsumer()
    );
  }

}
