package com.tinatiel.obschatbot.core.client.twitch.chat.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Provides an entrypoint for submitting Twitch Chat Client lifecycle messages.
 */
@MessagingGateway
public interface TwitchClientLifecycleGateway {

  @Gateway(requestChannel = "twitchClientLifecycleChannel")
  void submit(ObsChatbotEvent event);
}
