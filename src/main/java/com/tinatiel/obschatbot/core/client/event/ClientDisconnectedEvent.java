/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents when a client has closed the socket/connection with the remote server.
 */
public class ClientDisconnectedEvent extends AbstractObsChatbotEvent {

  private final String reason;

  public ClientDisconnectedEvent() {
    super();
    this.reason = null;
  }

  public ClientDisconnectedEvent(String reason) {
    super();
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "DisconnectedEvent{"
      + "reason='" + reason + "', "
      + super.toString()
      + "}";
  }
}
