package com.tinatiel.obschatbot.core.client.obs.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface ObsClientLifecycleGateway {
  @Gateway(requestChannel = "obsClientLifecycleChannel")
  void submit(ObsChatbotEvent event);
}
