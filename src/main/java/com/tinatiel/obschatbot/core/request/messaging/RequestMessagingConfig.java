package com.tinatiel.obschatbot.core.request.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@IntegrationComponentScan
@Configuration
public class RequestMessagingConfig {

  @Bean
  MessageChannel commandRequestChannel() {
    return new PublishSubscribeChannel();
  }

  @Bean
  MessageChannel actionRequestChannel() {
    return new PublishSubscribeChannel();
  }

  @Bean
  MessageChannel actionRequestStatusChannel() { return new PublishSubscribeChannel(); }

}
