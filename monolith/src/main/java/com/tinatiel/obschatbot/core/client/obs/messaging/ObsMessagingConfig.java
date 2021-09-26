package com.tinatiel.obschatbot.core.client.obs.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

/**
 * Encapsulates configuration for OBS client lifecycle messaging.
 */
@IntegrationComponentScan
@Configuration
public class ObsMessagingConfig {

  @Bean
  MessageChannel obsClientLifecycleChannel() {
    return new PublishSubscribeChannel();
  }

}
