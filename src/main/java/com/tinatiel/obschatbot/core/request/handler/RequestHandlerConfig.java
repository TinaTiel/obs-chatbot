package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.request.gateway.CommandRequestGateway;
import com.tinatiel.obschatbot.core.request.factory.CommandRequestFactory;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatHandlerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// TODO: Get rid of this, it adds very strange/confusing dependency patterns
@Import(ChatHandlerConfig.class)
@Configuration
public class RequestHandlerConfig {

  @Autowired
  CommandRequestFactory commandRequestFactory;

//  @Autowired
//  QueueClient<CommandRequest> commandRequestQueueClient;
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
