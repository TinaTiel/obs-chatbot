/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents when a client has connected to a remote server. Note, this is distinct from
 * authenticating and refers more-so to the raw sockets themselves; e.g. is the domain resolvable.
 */
public class ClientConnectedEvent extends AbstractObsChatbotEvent {

  public ClientConnectedEvent() {
    super();
  }

  @Override
  public String toString() {
    return "ConnectedEvent{" + super.toString() + "}";
  }
}
