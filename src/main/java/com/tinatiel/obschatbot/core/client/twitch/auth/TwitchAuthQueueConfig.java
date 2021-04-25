package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.messaging.QueueClientImpl;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.messaging.QueueNotifierImpl;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitchAuthQueueConfig {

  @Bean
  QueueClient<ObsChatbotEvent> twitchAuthQueueClient() {
    return new QueueClientImpl(twitchAuthQueue());
  }

  @Bean
  BlockingQueue<ObsChatbotEvent> twitchAuthQueue() {
    return new LinkedBlockingQueue<>();
  }

  @Bean
  QueueNotifier<ObsChatbotEvent> twitchAuthQueueNotifier() {
    QueueNotifier<ObsChatbotEvent> notifier = new QueueNotifierImpl(twitchAuthQueue());
    notifier.addListener((event -> {
      twitchAuthAuditQueue().add(event);
    }));

    return notifier;
  }

  @Bean
  BlockingQueue<ObsChatbotEvent> twitchAuthAuditQueue() {
    return new LinkedBlockingQueue<>();
  }

}
