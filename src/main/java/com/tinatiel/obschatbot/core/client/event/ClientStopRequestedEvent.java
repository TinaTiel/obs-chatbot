/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Acknowledges a request to stop a client. A client may subsequently follow this with an ${@link
 * ClientRequestIgnoredEvent} if, for example, the client is already stopped or stopping.
 */
public class ClientStopRequestedEvent extends AbstractObsChatbotEvent {

  private final String reason;

  public ClientStopRequestedEvent(String reason) {
    super();
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "StopRequestedEvent{"
      + super.toString()
      + "reason='" + reason + '\''
      + '}';
  }
}
