package com.tinatiel.obschatbot.core.request.messaging;

import com.tinatiel.obschatbot.core.request.CommandRequest;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Provides an access point to submitting {@link CommandRequest}s.
 */
@MessagingGateway
public interface CommandRequestGateway {

  @Gateway(requestChannel = "commandRequestChannel")
  void submit(CommandRequest commandRequest);
}
