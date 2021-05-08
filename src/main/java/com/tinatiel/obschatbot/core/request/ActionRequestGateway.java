package com.tinatiel.obschatbot.core.request;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface ActionRequestGateway {
  @Gateway(requestChannel = "actionRequestChannel")
  void submit(ActionRequest actionRequest);
}
