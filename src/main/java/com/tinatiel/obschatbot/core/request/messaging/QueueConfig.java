package com.tinatiel.obschatbot.core.request.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

/**
 * Encompasses all instances of Queues and QueueClients.
 */
@Configuration
public class QueueConfig {

//  // ====== CommandRequest Queue ====== //
//  @Bean
//  BlockingQueue<CommandRequest> commandRequestQueue() {
//    return new LinkedBlockingQueue<CommandRequest>();
//  }
//
//  @Bean
//  QueueClient<CommandRequest> commandRequestQueueClient() {
//    return new QueueClientImpl(commandRequestQueue());
//  }
//
//  // ====== ActionRequest Queue ====== //
//  @Bean
//  BlockingQueue<ActionRequest> actionRequestQueue() {
//    return new LinkedBlockingQueue<>();
//  }
//
//  @Bean
//  QueueClient<ActionRequest> actionRequestQueueClient() {
//    return new QueueClientImpl(actionRequestQueue());
//  }

  @Bean
  MessageChannel commandRequestChannel() {
    return new DirectChannel();
  }

  @Bean
  MessageChannel actionRequestChannel() {
    return new DirectChannel();
  }

}
