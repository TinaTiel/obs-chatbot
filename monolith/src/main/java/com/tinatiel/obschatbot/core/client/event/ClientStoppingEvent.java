/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents that a stop request has been acknowledged and is now being acted upon.
 */
public class ClientStoppingEvent extends AbstractObsChatbotEvent {

  public ClientStoppingEvent() {
    super();
  }

  @Override
  public String toString() {
    return "StoppingEvent{" + super.toString() + "}";
  }
}
