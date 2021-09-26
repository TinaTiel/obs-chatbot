/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents when, if applicable, a client in joining a remote server -- for example, when an IRC
 * client has started to join an IRC server and may be requesting additional capabilities (CAP).
 */
public class ClientJoiningEvent extends AbstractObsChatbotEvent {

  private final String message;

  public ClientJoiningEvent(String message) {
    super();
    this.message = message;
  }

  @Override
  public String toString() {
    return "JoiningEvent{"
      + super.toString()
      + "message=" + message
      + "}";
  }
}
