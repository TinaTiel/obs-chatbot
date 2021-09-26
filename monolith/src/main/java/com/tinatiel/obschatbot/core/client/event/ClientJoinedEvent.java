/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents when, if applicable, a client has joined a remote server -- for example, when an IRC
 * client has joined an IRC server.
 */
public class ClientJoinedEvent extends AbstractObsChatbotEvent {

  public ClientJoinedEvent() {
    super();
  }

  @Override
  public String toString() {
    return "JoinedEvent{" + super.toString() + "}";
  }
}
