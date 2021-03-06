/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Acknowledges that a start request has been submitted for a client. A client may subsequently
 * follow this with a ${@link ClientRequestIgnoredEvent} if, for example, the client is already
 * running or starting.
 */
public class ClientStartRequestedEvent extends AbstractObsChatbotEvent {

  public ClientStartRequestedEvent() {
    super();
  }

  @Override
  public String toString() {
    return "StartRequestedEvent{" + super.toString() + "}";
  }
}
