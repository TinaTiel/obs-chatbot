package com.tinatiel.obschatbot.core.client.twitch.chat.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

/**
 * Configures messaging for the the Twitch Chat Client.
 */
@IntegrationComponentScan
@Configuration
public class TwitchChatClientMessagingConfig {

  @Autowired
  TwitchClientLifecycleGateway twitchClientLifecycleGateway;

  @Bean
  MessageChannel twitchClientLifecycleChannel() {
    return new PublishSubscribeChannel();
  }

}
