package com.tinatiel.obschatbot.core.client.obs.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Provides an entrypoint for submitting lifecycle messages for the OBS Client.
 */
@MessagingGateway
public interface ObsClientLifecycleGateway {

  @Gateway(requestChannel = "obsClientLifecycleChannel")
  void submit(ObsChatbotEvent event);
}
