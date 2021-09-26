package com.tinatiel.obschatbot.core.request.messaging;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Provides an access point to submitting {@link ActionRequest}s.
 */
@MessagingGateway
public interface ActionRequestGateway {

  @Gateway(requestChannel = "actionRequestChannel")
  void submit(ActionRequest actionRequest);
}
