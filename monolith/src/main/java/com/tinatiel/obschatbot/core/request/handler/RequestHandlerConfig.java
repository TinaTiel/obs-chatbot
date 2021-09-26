package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.request.factory.CommandRequestFactory;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatHandlerConfig;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Aggregates all configuration related to request-handling.
 */
@Import(ChatHandlerConfig.class)
@Configuration
public class RequestHandlerConfig {

  @Autowired
  CommandRequestFactory commandRequestFactory;

  @Autowired
  CommandRequestGateway commandRequestGateway;

  @Bean
  CommandRequestDispatcher commandRequestDispatcher() {
    return new CommandRequestDispatcherImpl(
      commandRequestFactory,
      commandRequestGateway
    );
  }

}
