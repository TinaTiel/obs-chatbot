package com.tinatiel.obschatbot.core.client.twitch.auth.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@IntegrationComponentScan
@Configuration
public class TwitchAuthMessagingConfig {

  @Bean
  MessageChannel twitchAuthMessageChannel() {
    return new DirectChannel();
  }

  @ServiceActivator(inputChannel = "twitchAuthMessageChannel")
  @Bean
  MessageHandler twitchAuthEventLogger() {
    return new LoggingHandler("info");
  }

}
