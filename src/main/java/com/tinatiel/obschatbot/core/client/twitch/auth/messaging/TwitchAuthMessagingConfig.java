package com.tinatiel.obschatbot.core.client.twitch.auth.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@IntegrationComponentScan
@Configuration
public class TwitchAuthMessagingConfig {

  @Bean
  MessageChannel twitchAuthMessageChannel() {
    return new DirectChannel();
  }

}
