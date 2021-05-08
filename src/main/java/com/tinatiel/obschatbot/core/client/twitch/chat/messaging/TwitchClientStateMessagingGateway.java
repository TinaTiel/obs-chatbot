package com.tinatiel.obschatbot.core.client.twitch.chat.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface TwitchClientStateMessagingGateway {
  @Gateway(requestChannel = "twitchClientLifecycleChannel")
  void submit(ObsChatbotEvent event);
}
