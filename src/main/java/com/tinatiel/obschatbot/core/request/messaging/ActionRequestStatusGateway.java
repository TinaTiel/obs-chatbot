package com.tinatiel.obschatbot.core.request.messaging;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.request.ActionCompleteEvent;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface ActionRequestStatusGateway {
  @Gateway(requestChannel = "actionRequestStatusChannel")
  void submit(ActionCompleteEvent event);
}
