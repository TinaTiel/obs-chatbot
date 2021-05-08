package com.tinatiel.obschatbot.core.request;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface CommandRequestGateway {
  @Gateway(requestChannel = "commandRequestChannel")
  void submit(CommandRequest commandRequest);
}
