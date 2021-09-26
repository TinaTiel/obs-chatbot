package com.tinatiel.obschatbot.core;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

@Configuration
public class SpringIntegrationTestConfig {

  @Bean
  Queue<Message<?>> testChannelQueue() {
    return new LinkedBlockingQueue<>();
  }

  @Bean
  QueueChannel testChannel() {
    return new QueueChannel(testChannelQueue());
  }

}
