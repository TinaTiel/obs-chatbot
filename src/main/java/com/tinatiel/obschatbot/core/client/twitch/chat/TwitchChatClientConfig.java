/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.messaging.QueueClientImpl;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.messaging.QueueNotifierImpl;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLSocketFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Encompasses all configuration for the Twitch IRC chat client.
 */
@Configuration
public class TwitchChatClientConfig {

  @Autowired
  ChatRequestHandler chatRequestHandler;

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

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
    return new TwitchChatClientSettingsFactory(settings, authorizedClientService);
  }

  @Bean
  SSLSocketFactory sslSocketFactory() {
    return new UtilSSLSocketFactory();
  }

  @Bean
  PircBotxListener pircBotxListener() {
    return new PircBotxListener(
        twitchChatEventQueueClient(),
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
  ClientManager twitchChatClientManager() {
    return new TwitchChatClientManager(twitchChatEventQueueClient(), twitchChatClientFactory());
  }

  @Bean
  BlockingQueue<ObsChatbotEvent> twitchChatEventQueue() {
    return new LinkedBlockingQueue<>();
  }

  @Bean
  QueueClient<ObsChatbotEvent> twitchChatEventQueueClient() {
    return new QueueClientImpl(twitchChatEventQueue());
  }

  @Bean
  QueueNotifier<ObsChatbotEvent> twitchChatEventQueueNotifier() {
    QueueNotifier<ObsChatbotEvent> notifier = new QueueNotifierImpl(twitchChatEventQueue());
    notifier.addListener(eventLogger());
    notifier.addListener(twitchChatClientManager());

    return notifier;
  }

  Listener<ObsChatbotEvent> eventLogger() {
    return new Listener<ObsChatbotEvent>() {

      private final Logger log = LoggerFactory.getLogger(this.getClass());

      @Override
      public void onEvent(ObsChatbotEvent event) {
        log.debug("Logged Event: " + event.toString());
      }
    };
  }

  @Bean
  Listener<ActionRequest> twitchChatActionRequestListener() {
    return new TwitchChatActionRequestListener(twitchChatClientManager());
  }

}
