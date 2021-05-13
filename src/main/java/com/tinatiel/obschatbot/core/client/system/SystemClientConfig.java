package com.tinatiel.obschatbot.core.client.system;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.request.messaging.ActionRequestStatusGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemClientConfig {

  @Autowired
  ActionRequestStatusGateway actionRequestStatusGateway;

  @Bean
  ClientManager waitClientManager() {
    return new SystemClientManager(actionRequestStatusGateway);
  }

}
