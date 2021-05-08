package com.tinatiel.obschatbot.core.client.twitch.chat.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@IntegrationComponentScan
@Configuration
public class TwitchChatClientMessagingConfig {

  @Bean
  MessageChannel twitchClientLifecycleChannel() {
    return new DirectChannel();
  }

  @Autowired
  TwitchClientMessagingGateway twitchClientMessagingGateway;

}
