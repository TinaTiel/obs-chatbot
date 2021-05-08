package com.tinatiel.obschatbot.core.client.twitch.auth.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface TwitchAuthClientMessagingGateway {
  @Gateway(requestChannel = "twitchAuthMessageChannel")
  void submit(ObsChatbotEvent event);
}
